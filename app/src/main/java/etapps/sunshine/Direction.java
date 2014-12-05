package etapps.sunshine;

import android.content.Context;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Direction {
    private static final String TAG = "Direction";
    // compass arrow to rotate
    public ImageView arrowView = null;
    public float angle;
    private float currectAzimuth = 0;
    public Direction(Context context) {
    }
    public void adjustArrow(float angle) {
        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }
        Animation an = new RotateAnimation(-currectAzimuth, angle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        an.setDuration(400);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        arrowView.startAnimation(an);
    }
}
