package org.liuyehcf.saima.toutiao;

/**
 * Created by HCF on 2017/4/18.
 */
import java.util.*;

public class Item1{
    private static final class Node{
        int key;
        int pos;
        public Node(int key,int pos){
            this.key=key;
            this.pos=pos;
        }
    }

    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        int m=scanner.nextInt();
        int[] arym=new int[m];
        for(int i=0;i<m;i++){
            arym[i]=scanner.nextInt();
        }
        int n=scanner.nextInt();
        int iter=0;
        Map<Integer,Integer> map=new HashMap<Integer,Integer>();
        while(iter<n){
            int next=scanner.nextInt();
            if(!map.containsKey(next)){
                map.put(next,iter);
            }
            iter++;
        }

        List<Node> list=new ArrayList<Node>();
        for(int i=0;i<m;i++){
            if(map.containsKey(arym[i])){
                list.add(new Node(arym[i],map.get(arym[i])));
                map.remove(arym[i]);
            }
        }

        Collections.sort(list,new Comparator<Node>(){
            public int compare(Node obj1,Node obj2){
                return obj1.pos-obj2.pos;
            }
        });

        StringBuilder sb=new StringBuilder();
        for(Node node:list){
            sb.append(node.key+" ");
        }
        if(sb.length()>0) sb.setLength(sb.length()-1);
        System.out.println(sb.toString());
    }
}