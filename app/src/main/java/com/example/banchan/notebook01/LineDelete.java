package com.example.banchan.notebook01;

import java.util.ArrayList;

//  Created by Banchan on 2016/03/27

public final class LineDelete { //  継承されるようなものでない

    static Integer newCurPositon;
    static Integer startPos;
    static Integer endPos;
    static String preStr;
    static Integer PreCurPositon;

    static void PositionSet(String str, int curPos){
        //  カーソル位置のある行を特定する⇒行削除、前・後削除に渡す

        newCurPositon = curPos;
        preStr = str;
        str +="\n";     //  カーソル範囲を決めるために末尾に改行を付ける
        ArrayList<Integer> nPos = new ArrayList<Integer>();
        startPos =0;
        endPos = 0;
        try {
            //  元文字列を1文字づつ調べ、改行位置を配列にする
            Integer i=0;
            for ( ; i <= str.length() - 1; i++) {
                if (str.substring(i,i+1).equals("\n")) {
                    nPos.add(i);
                }
            }
            //  改行位置とカーソル位置を比べて削除する行位置を決める
            for(int j =0 ; j < nPos.size() ; j++){
                if(nPos.get(j) < curPos){
                    startPos = nPos.get(j);
                }
                else{
                    endPos = nPos.get(j);
                    break;
                }
            }
            // Toast.makeText(ActUpdate.this,startPos.toString() +":" + endPos.toString(), Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            //return e.gMessage();
        }

    }


    static String deleThisLine (String str,int curPos){
        //  カーソル行を削除
        PositionSet(str,curPos);
        String rtn="";
        //  除外範囲を除いて返り値を一文字づつ繋げてゆく
        for (int k=0 ; k <=str.length() -1 ; k++ ){
            if(k < startPos || k>=endPos) {     //  endPosには\nがあるので残しておく
                rtn += str.substring(k, k + 1);
            }
        }
        newCurPositon = startPos;
        return  rtn.trim();
    }

    static String deleteAfterCur(String str, int curPos){
        //  カーソル行を含めた後の行を削除
        PositionSet(str,curPos);
        String rtn="";
        //  除外範囲を除いて返り値を一文字づつ繋げてゆく
        for (int k=0 ; k <=str.length() -2 ; k++ ){
            if(k < startPos ) {     //
                rtn += str.substring(k, k + 1);
            }
        }
        newCurPositon = startPos;
        return rtn;
    }

    static String deletePreCur(String str, int curPos){
        //  カーソル行を含めた前の行を削除
        PositionSet(str,curPos);
        String rtn="";
        //  除外範囲を除いて返り値を一文字づつ繋げてゆく
        for (int k=0 ; k <=str.length() -2 ; k++ ){
            if( k>endPos) {     //
                rtn += str.substring(k, k + 1);
            }
        }
        newCurPositon = 0;
        return rtn;
    }
}
