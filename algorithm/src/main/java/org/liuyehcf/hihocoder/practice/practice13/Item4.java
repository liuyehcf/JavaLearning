package org.liuyehcf.hihocoder.practice.practice13;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by liuye on 2017/4/9 0009.
 */


public class Item4 {
    private static class Position{
        int R;
        int C;
        public Position(int R,int C){
            this.R=R;
            this.C=C;
        }
    }

    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        int N=scanner.nextInt();
        int R=scanner.nextInt();
        int C=scanner.nextInt();

        List<List<Position>> lst=new ArrayList<List<Position>>();
        lst.add(Arrays.asList(new Position(R,C)));
        for(int i=1;i<=N;i++){
            List<Position> cur=new ArrayList<Position>();
            for(Position p:lst.get(i-1)){
                cur.add(new Position(p.R+1,p.C+2));
                cur.add(new Position(p.R+2,p.C+1));

                if(p.R-1>=1){
                    cur.add(new Position(p.R-1,p.C+2));
                }
                if(p.R-2>=1){
                    cur.add(new Position(p.R-2,p.C+1));
                }
                if(p.R-2>=1&&p.C-1>=1){
                    cur.add(new Position(p.R-2,p.C-1));
                }
                if(p.R-1>=1&&p.C-2>=1){
                    cur.add(new Position(p.R-1,p.C-2));
                }
                if(p.C-2>=1){
                    cur.add(new Position(p.R+1,p.C-2));
                }
                if(p.C-1>=1){
                    cur.add(new Position(p.R+2,p.C-1));
                }
            }
            lst.add(cur);
        }
        System.out.println(lst.get(N).size()%1000000007);
    }
}

class Item4_1 {
    private static class Position{
        int R;
        int C;
        public Position(int R,int C){
            this.R=R;
            this.C=C;
        }
    }

    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        int N=scanner.nextInt();
        int R=scanner.nextInt();
        int C=scanner.nextInt();

        List<List<Position>> lst=new ArrayList<List<Position>>();
        lst.add(Arrays.asList(new Position(R,C)));
        for(int i=1;i<=N;i++){
            List<Position> cur=new ArrayList<Position>();
            for(Position p:lst.get(i-1)){
                cur.add(new Position(p.R+1,p.C+2));
                cur.add(new Position(p.R+2,p.C+1));

                if(p.R-1>=1){
                    cur.add(new Position(p.R-1,p.C+2));
                }
                if(p.R-2>=1){
                    cur.add(new Position(p.R-2,p.C+1));
                }
                if(p.R-2>=1&&p.C-1>=1){
                    cur.add(new Position(p.R-2,p.C-1));
                }
                if(p.R-1>=1&&p.C-2>=1){
                    cur.add(new Position(p.R-1,p.C-2));
                }
                if(p.C-2>=1){
                    cur.add(new Position(p.R+1,p.C-2));
                }
                if(p.C-1>=1){
                    cur.add(new Position(p.R+2,p.C-1));
                }
            }
            lst.add(cur);
        }
        System.out.println(lst.get(N).size()%1000000007);
    }
}
