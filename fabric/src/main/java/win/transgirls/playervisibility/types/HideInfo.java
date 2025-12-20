package win.transgirls.playervisibility.types;

import win.transgirls.playervisibility.config.ModConfig;

public class HideInfo {
    public final boolean hide;
    public final float alpha;

    public HideInfo(boolean hide, double distance) {
        if (!hide) {
            this.hide = false;
            this.alpha = 1.0f;
            return;
        }

        if (ModConfig.comfortZone) {
            double distanceActual = Math.sqrt(distance);

            if (distanceActual <= ModConfig.comfortDistance) {
                this.hide = true;
                this.alpha = 0.0f;
                return;
            }

            double transitionEnd = ModConfig.comfortDistance + ModConfig.comfortFalloff;
            if (distanceActual <= transitionEnd) {
                double transitionStart = ModConfig.comfortDistance; //🏳️‍⚧️
                double falloffAmount = Math.min(Math.max(
                        (distanceActual - transitionStart) / (transitionEnd - transitionStart),
                        0), 1);

                this.hide = true;
                this.alpha = (float) falloffAmount;
                return;
            }

            this.hide = false;
            this.alpha = 1.0f;
            return;
        }

        this.hide = true;
        this.alpha = 0.0f;
    }
}