package dev.xnasuni.playervisibility.types;

import static dev.xnasuni.playervisibility.PlayerVisibility.transparency;

import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;

public class TransparentVertexConsumerProvider<E extends Entity> implements VertexConsumerProvider {
    private final VertexConsumerProvider parent;
    private final E entity;

    public TransparentVertexConsumerProvider(VertexConsumerProvider parent, E entity) {
        this.parent = parent;
        this.entity = entity;

        if (!transparency.containsKey(entity)) {
            transparency.put(entity, -1);
        }
    }

    @Override public VertexConsumer getBuffer(RenderLayer layer) {
        VertexConsumer original = parent.getBuffer(layer);
        return new TransparentVertexConsumer<>(original, entity);
    }
}

/*
so this does not work, I coded all this for nothing, trying to change the render layer in between like something
ends up just making the client crash Caused by: java.lang.IllegalStateException: Not filled all elements of the vertex

so I give up
ill MAYBE try fixing this in the future it doesn't even look that bad to care about it so much
i just really wanted it to be smooth

also yes I had a debug key thing to print stuff in render for debugging "PlayerVisibility.debugKey"

TIME WASTED DEBUGGING: 3 hours and 27 minutes

if (transparency.get(entity) != -1 && layer instanceof RenderLayer.MultiPhase) { // this is done to make culling work behind entities
    original = parent.getBuffer(RenderLayer.getTranslucent());
    RenderLayer.MultiPhase multiPhase = (RenderLayer.MultiPhase) layer;
    try { // all versions since blaze3d introduction (1.14) up to 1.21.4+
        Field textureField = multiPhase.phases.getClass().getDeclaredField("field_21406");
        textureField.setAccessible(true);

        RenderPhase.Texture _texture = (RenderPhase.Texture) textureField.get(multiPhase.phases);

        Field idField = _texture.getClass().getDeclaredField("field_21397");
        idField.setAccessible(true);

        Optional<Identifier> texture = (Optional<Identifier>) idField.get(_texture);
        if (texture.isPresent()) {
            original = parent.getBuffer(RenderLayer.getEntityTranslucentCull(texture.get()));
        } else {
            original = parent.getBuffer(layer);
        }
    } catch (Throwable e) { // >=1.17 fallback, shouldn't ever fail though
        if (PlayerVisibility.debugKey) {
            PlayerVisibilityClient.LOGGER.warn("First Exception", e);
        }
        try {
            Field textureField = multiPhase.phases.getClass().getDeclaredField("field_21406");
            textureField.setAccessible(true);

            RenderPhase.Texture _texture = (RenderPhase.Texture) textureField.get(multiPhase.phases);

            Method getIdMethod = _texture.getClass().getDeclaredMethod("method_23564");
            getIdMethod.setAccessible(true);

            Optional<Identifier> texture = (Optional<Identifier>) getIdMethod.invoke(_texture);
            if (texture.isPresent()) {
                original = parent.getBuffer(RenderLayer.getEntityTranslucentCull(texture.get()));
            } else {
                throw new IllegalStateException("No texture in layer!");
            }
        } catch (Throwable e2) {
            if (PlayerVisibility.debugKey) {
                PlayerVisibilityClient.LOGGER.warn("Second Exception", e2);
            }
            original = parent.getBuffer(layer);
        }
    }
} else {
    original = parent.getBuffer(layer);
}
*/