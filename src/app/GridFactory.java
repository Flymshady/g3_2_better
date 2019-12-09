package app;

import lwjglutils.OGLBuffers;

public class GridFactory {

    public static OGLBuffers generateGrid(int m, int n) {

        float[] vb = new float[m*n*2];
        int index=0;
        for (int j =0;j<n;j++){
            for(int i=0;i<m;i++){
                vb[index++] = i /(float) (m-1);
                vb[index++] = j /(float) (n-1);
            }
        }

        int[] ib = new int[(m-1)*(n-1)*2*3];
        int index2=0;
        for(int j=0;j<n-1;j++) {
            int row =j*m;
            for (int i = 0; i < m - 1; i++) {
                ib[index2++]=(row+i);
                ib[index2++]= (row+i+1);
                ib[index2++]=(row+i+m);

                ib[index2++]=(row+i+m);
                ib[index2++]= (row+i+1);
                ib[index2++]= (row+i+m+1);
            }
        }

        OGLBuffers.Attrib[] atribs = new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2)
        };

        return new OGLBuffers(vb, atribs, ib);
    }
}
