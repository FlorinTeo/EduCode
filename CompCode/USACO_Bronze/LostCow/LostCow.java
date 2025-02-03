/*
ID: florinteo
PROG: LostCow
LANG: Java             
*/
/*
DEF: https://usaco.org/index.php?page=viewproblem2&cpid=735
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class LostCow {
    public static void main(String[] args) throws IOException {
        // #region load data
        BufferedReader br = new BufferedReader(new FileReader("USACO_Bronze/LostCow/lostcow.in"));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        br.close();
        // #endregion load data

        // #region algorithm
        int totalDistance = 0;
        int step = 1;
        int crtX = x;
        int newX = x + step;
        while((y - crtX) * (y - newX) > 0) {
            totalDistance += Math.abs(newX - crtX);
            crtX = newX;
            step *= -2;
            newX = x + step;
        }
        totalDistance += Math.abs(y - crtX);
        // #endregion algorithm

        // #region write output
        PrintWriter pw = new PrintWriter("USACO_Bronze/LostCow/lostcow.out");
        pw.println(totalDistance);
        pw.flush();
        pw.close();
        // #endregion write output
    }
}
