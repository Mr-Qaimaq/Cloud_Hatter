package edu.msu.cse476.cloudhatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.msu.cse476.cloudhatter.Cloud.Models.Hat;

/**
 * The view we will draw out hatter in
 */
public class HatterView extends View {
    /*
     * The ID values for each of the hat types. The values must
     * match the index into the array hats_spinner in arrays.xml.
     */
    public static final int HAT_BLACK = 0;
    public static final int HAT_GRAY = 1;
    public static final int HAT_CUSTOM = 2;
    /**
     * The image bitmap. None initially.
     */
    private Bitmap imageBitmap = null;
    /**
     * Image drawing scale
     */
    private float imageScale = 1;

    /**
     * Image left margin in pixels
     */
    private float marginLeft = 0;

    /**
     * Image top margin in pixels
     */
    private float marginTop = 0;
    /**
     * The bitmap to draw the hat
     */
    private Bitmap hatBitmap = null;

    /**
     * the bitmap to draw the feather
     */
    private Bitmap featherBitmap = null;

    /**
     * The bitmap to draw the hat band. We draw this
     * only when drawing the custom color hat, so we
     * don't color the hat band
     */
    private Bitmap hatbandBitmap = null;
    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    /**
     * Second touch status
     */
    private Touch touch2 = new Touch();
    /**
     * Paint to use when drawing the custom color hat
     */
    private Paint customPaint;
    private Context myContext;
    /**
     * The current parameters
     */
    private Parameters params = new Parameters();

    private static class Parameters implements Serializable {
        /**
         * Path to the image file if one exists
         */
        public String imageUri = null;
        /**
         * The current hat type
         */
        public int hat = HAT_BLACK;
        /**
         * X location of hat relative to the image
         */
        public float hatX = 0;

        /**
         * Y location of hat relative to the image
         */
        public float hatY = 0;

        /**
         * Hat scale, also relative to the image
         */
        public float hatScale = 0.25f;

        /**
         * Hat rotation angle
         */
        public float hatAngle = 0;
        /**
         * Custom hat color
         */
        public int color = Color.CYAN;
        /*
         * Do we draw a feather?
         */
        public boolean drawthefeather = false;


    }
    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private static class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;
        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;
        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }

    public HatterView(Context context) {
        super(context);
        init(context);
    }

    public HatterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
   }

    public HatterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setHat(HAT_BLACK);
        customPaint = new Paint();
        customPaint.setColorFilter(new LightingColorFilter(params.color, 0));
        myContext = context;
    }

    /**
     * Handle a draw event
     * @param canvas canvas to draw on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // If there is no image to draw, we do nothing
        if(imageBitmap == null) {
            return;
        }

        /*
         * Determine the margins and scale to draw the image
         * centered and scaled to maximum size on any display
         */
        // Get the canvas size
        float wid = getWidth();
        float hit = getHeight();

        // What would be the scale to draw the where it fits both
        // horizontally and vertically?
        float scaleH = wid / imageBitmap.getWidth();
        float scaleV = hit / imageBitmap.getHeight();

        // Use the lesser of the two
        imageScale = Math.min(scaleH, scaleV);

        // What is the scaled image size?
        float iWid = imageScale * imageBitmap.getWidth();
        float iHit = imageScale * imageBitmap.getHeight();

        // Determine the top and left margins to center
        marginLeft = (wid - iWid) / 2;
        marginTop = (hit - iHit) / 2;

        /*
         * Draw the image bitmap
         */
        canvas.save();
        canvas.translate(marginLeft,  marginTop);
        canvas.scale(imageScale, imageScale);
        canvas.drawBitmap(imageBitmap, 0, 0, null);
        /*
         * Draw the hat
         */
        canvas.translate(params.hatX,  params.hatY);
        canvas.scale(params.hatScale, params.hatScale);
        canvas.rotate(params.hatAngle);

        if(params.hat == HAT_CUSTOM) {
            canvas.drawBitmap(hatBitmap, 0, 0, customPaint);
        } else {
            canvas.drawBitmap(hatBitmap, 0, 0, null);
        }
        if(params.drawthefeather) {
            // Android scaled images that it loads. The placement of the
            // feather is at 322, 22 on the original image when it was
            // 500 pixels wide. It will have to move based on how big
            // the hat image actually is.
            float factor = hatBitmap.getWidth() / 500.0f;
            canvas.drawBitmap(featherBitmap, 322 * factor, 22 * factor, null);
        }

        if(hatbandBitmap != null) {
            canvas.drawBitmap(hatbandBitmap, 0, 0, null);
        }
        canvas.restore();
    }
    /**
     * Get the installed image path
     * @return path or null if none
     */
    public String getImageUri() {
        return params.imageUri;
    }

    /**
     * Set an image URI based on a string representation.
     * @param imageUri Uri for the image file
     */
    public void setImageUri(String imageUri) {
        // We'll clear the old URI until we know a new one
        imageBitmap = null;
        params.imageUri = "";

        if(imageUri != null) {
            Uri uri = Uri.parse(imageUri);
            setImageUri(uri);
        }

        invalidate();
    }

    /**
     * Set the image URI. Load an image from any source,
     * including external sources.
     * @param uri URI for the image
     */
    public void setImageUri(final Uri uri) {
        final String scheme = uri.getScheme();
        if(scheme == null) {
            // If no scheme, we have no image
            imageBitmap = null;
            params.imageUri = "";
            return;
        }

        new Thread(new Runnable() {

            /**
             * Run the thread that loads the image
             */
            @Override
            public void run() {

                boolean success = false;
                try {
                    // This code has been modified to load content either
                    // from a content provider (local) or an arbitrary URL
                    // (internet)
                    InputStream input;
                    int status;
                    URL url;
                    if(scheme.equals("content")) {
                        input = getContext().getContentResolver().openInputStream(uri);
                    } else {
                        boolean redirect = false;
                        url = new URL(uri.toString());
                        //input = url.openStream();
                        HttpURLConnection httpURLConnection;
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        //httpURLConnection.connect();
                        status = httpURLConnection.getResponseCode();

                        if (status != HttpURLConnection.HTTP_OK) {
                            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                                    || status == HttpURLConnection.HTTP_MOVED_PERM
                                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                                redirect = true;
                        }
                        if (redirect) {
                            String newUrl = httpURLConnection.getHeaderField("Location");
                            httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                        }
                        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            input = httpURLConnection.getInputStream();
                        } else {
                            imageBitmap = null;
                            params.imageUri = "";
                            toastException(myContext, "Error getting image: " + httpURLConnection.getResponseMessage());
                            return;
                        }
                    }

                    imageBitmap = BitmapFactory.decodeStream(input);
                    if (input != null) {
                        input.close();
                    }
                    params.imageUri = uri.toString();
                    success = true;
                } catch (SecurityException | IOException ex) {
                    toastException(myContext, "Error getting image: " + ex.getMessage());
                }

                if(!success) {
                    imageBitmap = null;
                    params.imageUri = "";
                }

                /**
                 * Post execute in the UI thread to invalidate and
                 * force a redraw.
                 **/
                post(new Runnable() {

                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }

        }).start();
    }

    private void toastException(Context context, final String msg) {
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(myContext,
                        msg,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Get the current custom hat color
     * @return hat color integer value
     */
    public int getColor() {
        return params.color;
    }

    /**
     * Set the current custom hat color
     * @param color hat color integer value
     */
    public void setColor(int color) {
        params.color = color;

        // Create a new filter to tint the bitmap
        customPaint.setColorFilter(new LightingColorFilter(color, 0));
        invalidate();
    }

     /**
     * Get the current hat type
     * @return one of the hat type values HAT_BLACK, etc.
     */
    public int getHat() {
        return params.hat;
    }

    /**
     * Set the hat type
     * @param hat hat type value, HAT_BLACK, etc.
     */
    public void setHat(int hat) {
        params.hat = hat;
        hatBitmap = null;
        hatbandBitmap = null;

        switch(hat) {
            case HAT_BLACK:
                hatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hat_black);
                break;

            case HAT_GRAY:
                hatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hat_gray);
                break;

            case HAT_CUSTOM:
                hatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hat_white);
                hatbandBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hat_white_band);
                break;
        }

        invalidate();
    }
    public boolean getFeather() { return params.drawthefeather;}

    public void setFeather(boolean feather) {
        params.drawthefeather = feather;
        if (featherBitmap == null) {
            featherBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.feather);
        }
        invalidate();
    }
    /**
     * Handle a touch event
     * @param event The touch event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                touch1.copyToLast();
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                if(touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    touch2.copyToLast();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(id == touch2.id) {
                    touch2.id = -1;
                } else if(id == touch1.id) {
                    // Make what was touch2 now be touch1 by
                    // swapping the objects.
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:

                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                move();
                return true;
        }

        return super.onTouchEvent(event);
    }
    @Override
    public boolean performClick() {
        return super.performClick();
    }
    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Convert to image coordinates
            float x = (event.getX(i) - marginLeft) / imageScale;
            float y = (event.getY(i) - marginTop) / imageScale;

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }

        invalidate();
    }
    /**
     * Handle movement of the touches
     */
    private void move() {
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if(touch1.id < 0) {
            return;
        }

        // At least one touch
        // We are moving
        touch1.computeDeltas();

        params.hatX += touch1.dX;
        params.hatY += touch1.dY;

        if(touch2.id >= 0) {
            // Two touches

            /*
             * Rotation
             */
            float angle1 = angle(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float angle2 = angle(touch1.x, touch1.y, touch2.x, touch2.y);
            float da = angle2 - angle1;
            rotate(da, touch1.x, touch1.y);
            /*
             * Scaling
             */
            float length1 = length(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float length2 = length(touch1.x, touch1.y, touch2.x, touch2.y);
            scale(length2 / length1, touch1.x, touch1.y);

        }
    }
    /**
     * Rotate the image around the point x1, y1
     * @param dAngle Angle to rotate in degrees
     * @param x1 rotation point x
     * @param y1 rotation point y
     */
    public void rotate(float dAngle, float x1, float y1) {
        params.hatAngle += dAngle;

        // Compute the radians angle
        double rAngle = Math.toRadians(dAngle);
        float ca = (float) Math.cos(rAngle);
        float sa = (float) Math.sin(rAngle);
        float xp = (params.hatX - x1) * ca - (params.hatY - y1) * sa + x1;
        float yp = (params.hatX - x1) * sa + (params.hatY - y1) * ca + y1;

        params.hatX = xp;
        params.hatY = yp;
    }
    /**
     * Scale the image around the point x1, y1
     * @param scale Scale factor
     * @param x1 scale point x
     * @param y1 scale point y
     */
    public void scale(float scale, float x1, float y1) {
        params.hatScale *= scale;

        // Compute a vector to hatX, hatY
        float dx = params.hatX - x1;
        float dy = params.hatY - y1;

        // Compute scaled hatX, hatY
        params.hatX = x1 + dx * scale;
        params.hatY = y1 + dy * scale;
    }
    /**
     * Determine the distance between two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed distance
     */
    private float length(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Determine the angle for two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed angle in degrees
     */
    private float angle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
    /**
     * Save the view state to a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putToBundle(String key, Bundle bundle) {
        bundle.putSerializable(key, params);

    }

    /**
     * Get the view state from a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(String key, Bundle bundle) {
        params = (Parameters)bundle.getSerializable(key);

        // Ensure the options are all set
        if (params != null) {
            setColor(params.color);
            setImageUri(params.imageUri);
            setHat(params.hat);
            setFeather(params.drawthefeather);
        }
    }
    public void reset() {
        params.hatX = 0;
        params.hatY = 0;
        params.hatScale = 0.25f;
        params.hatAngle = 0;
        invalidate();
    }
    public void loadHat(Hat hat) {
        // Create a new set of parameters
        final Parameters newParams = new Parameters();

        // Load into it
        newParams.imageUri = hat.getUri();
        newParams.hatX = hat.getX();
        newParams.hatY = hat.getY();
        newParams.hatAngle = hat.getAngle();
        newParams.hatScale = hat.getScale();
        newParams.color = hat.getColor();
        newParams.hat = hat.getType();
        newParams.drawthefeather = hat.getFeather().equals("yes");

        post(new Runnable() {

            @Override
            public void run() {
                params = newParams;

                // Ensure the options are all set
                setColor(params.color);
                setImageUri(params.imageUri);
                setHat(params.hat);
                setFeather(params.drawthefeather);

            }

        });

    }

    public void saveXml(String name, XmlSerializer xml) throws IOException {
        xml.startTag(null, "hatting");

        xml.attribute(null, "name", name);
        xml.attribute(null, "uri", params.imageUri != null ? params.imageUri : "");
        xml.attribute(null, "x", Float.toString(params.hatX));
        xml.attribute(null, "y", Float.toString(params.hatY));
        xml.attribute(null, "angle", Float.toString(params.hatAngle));
        xml.attribute(null, "scale", Float.toString(params.hatScale));
        xml.attribute(null,  "color", Integer.toString(params.color));
        xml.attribute(null, "hat", Integer.toString(params.hat));
        xml.attribute(null, "feather", params.drawthefeather ? "yes" : "no");

        xml.endTag(null,  "hatting");
    }
}