package newsimooc.imooc.com.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/10/12.
 */

public class ImageSplitterUtil {

    /**
     * @param bitmap
     * @param piece  切成piece * piece块
     * @return  List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {
        List<ImagePiece> imagePiecesList = new ArrayList<>();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //每一个块的宽度
        int pieceWidth = Math.min(width, height) / piece;
        for (int i=0; i<piece; i++) {
            for (int j=0; j<piece; j++) {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(i * piece + j);

                int x = j * pieceWidth;
                int y = i * pieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
                imagePiecesList.add(imagePiece);
            }
        }
        return imagePiecesList;
    }
}
