package org.liuyehcf.hihocoder.practice.practice13;

import java.util.Scanner;

/**
 * Created by liuye on 2017/4/9 0009.
 */
public class Item1 {
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        int N=scanner.nextInt();
        String[] names=new String[N];
        for(int i=0;i<N;i++){
            names[i]=scanner.next();
        }

        int offset='a'-'A';

        for(String name:names){
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<name.length();i++){
                char c=name.charAt(i);
                if(c>='A'&&c<='Z'){
                    sb.append("_"+(char)(c+offset));
                }
                else if(c=='_'){
                    char next=name.charAt(i+1);
                    sb.append((char)(next-offset));
                    i++;
                }
                else{
                    sb.append(c);
                }
            }
            System.out.println(sb.toString());
        }
    }
}
