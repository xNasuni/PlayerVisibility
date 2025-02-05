package dev.xnasuni.playervisibility.multiversion;

import dev.xnasuni.playervisibility.util.ArrayListUtil;
import static dev.xnasuni.playervisibility.PlayerVisibilityClient.LOGGER;

import java.io.IOException;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

public class VersionMixinPlugin implements IMixinConfigPlugin {
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return versionMixinCheck(targetClassName, mixinClassName);
    }
    public static boolean versionMixinCheck(String className, String mixinClass) {
        boolean testPassed = true;

        try {
            ClassNode target = MixinService.getService().getBytecodeProvider().getClassNode(className);
            ClassNode mixin = MixinService.getService().getBytecodeProvider().getClassNode(mixinClass);

            List<AnnotationNode> annotations = mixin.visibleAnnotations;
            List<MethodNode> methods = mixin.methods;

            if (annotations == null) {
                return true;
            }

            for (AnnotationNode annotation : annotations) {
                if (Type.getDescriptor(VersionedMixin.class).equals(annotation.desc)) {
                    List<String> targetVersions = Annotations.getValue(annotation, "value");
                    String currentVersion = getMinecraftVersion();

                    boolean localPassed = true;
                    for (String targetString : targetVersions) {
                        VersionString targetVersion = VersionString.of(targetString);
                        localPassed = localPassed && targetVersion.test(currentVersion);
                    }

                    testPassed = testPassed && localPassed;

                    if (testPassed) {
                        LOGGER.info("MultiVersion applying mixin {} patching {}, version range {}", mixin.name, target.name, ArrayListUtil.joinSeperator(annotation.values));
                    }
                }
            }
        } catch (IllegalArgumentException | ClassNotFoundException | IOException e) {
            LOGGER.error("Exception while trying to check MultiVersion mixin, not applying.", e);
            return false;
        }

        return testPassed;
    }
    public static String getMinecraftVersion() {
        return FabricLoader.getInstance().getModContainer("minecraft").orElseThrow(() -> new RuntimeException("FabricLoader.getInstance().getModContainer().orElseThrow() Optionable was null")).getMetadata().getVersion().getFriendlyString();
    }

    @Override public void onLoad(String mixinPackage) { }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public List<String> getMixins() {  return null; }
    @Override public String getRefMapperConfig() { return null; }
}