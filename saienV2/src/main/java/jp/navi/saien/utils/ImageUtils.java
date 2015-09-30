package jp.navi.saien.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class ImageUtils {
	
	public static byte[] getResizedImage(Context ctx, Uri uri, int samplingSize){
		byte[] bitmapdata = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = samplingSize; // Example, there are also ways to calculate an optimal value.
		InputStream in;
		try {
			in = ctx.getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			bitmapdata = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmapdata;
		
	}
	
	public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}

}
