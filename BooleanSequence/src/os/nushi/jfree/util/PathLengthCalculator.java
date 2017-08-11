package os.nushi.jfree.util;

import os.nushi.jfree.ds.primitive.CharStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Supporting chars: *, ., ?, (), +, \\n, {},[], (?:...),(?!...),(?=...),(?<!...),(?<=...)
 * To support: ,...
 */
//TODO: |
public class PathLengthCalculator {

    private List<Pair<Integer>> groups = new ArrayList<>();

    /**
     * Calculate the x length of a string statisfies this regular expression.
     * @param re regular expression
     * @return minimum length
     */
    public Pair length(char[] re){

        int minCounter=0;
        int maxCounter=0;

        for(int i=0;i<re.length;i++){
            if(re[i] == '('){
                char[] group = extractGroup(re,i);
                i+=group.length+1; //(abc) ; 5 length
                if(group[0] == '?'){
                    if(group[1] == '<'){
                        group= CharArrayUtil.subArray(group,3,group.length-1);
                    }else{
                        group= CharArrayUtil.subArray(group,2,group.length-1);
                    }
                }
                Pair<Integer> groupLength = length(group);

                if(i == re.length-1){
                    groups.add(groupLength);
                    minCounter+=groupLength.x;
                    maxCounter = setMaxLength(maxCounter,groupLength.y);
                }else if(re[i+1] == '?'){
                    groups.add(new Pair<>(0,groupLength.y));
                    i++;
                    maxCounter=setMaxLength(maxCounter,groupLength.y);;
                }else if(re[i+1] == '*' ){
                    groups.add(new Pair<>(0,-1));
                    i++;
                    maxCounter=-1;
                }else if(re[i+1] == '+'){
                    groups.add(new Pair<>(groupLength.y,-1));
                    i++;
                    minCounter=-1;
                }else if(re[i+1] == '{' ){
                    Triplet<Integer> result = extractRange(re, i+1);
                    i= result.z+1;
                    minCounter+=result.x*groupLength.x;
                    maxCounter= setMaxLength(maxCounter,result.y*groupLength.y);
                    groups.add(groupLength);

                }else{
                    groups.add(groupLength);
                    minCounter+=groupLength.x;
                    maxCounter= setMaxLength(maxCounter,groupLength.y);
                }

            }else if(re[i] == '\\'){

                char c=re[++i];
                if(CharUtil.isDigit(c)){
                    int len = (groups.size()+"").length();
                    String backRefNum = c+"";
                    for(int j=1;j<len;j++){
                        char nextDigit = re[j+i];
                        if(CharUtil.isDigit(nextDigit)){
                            backRefNum += nextDigit;
                        }else{
                            break;
                        }
                    }
                    if(Integer.parseInt(backRefNum) > groups.size() && backRefNum.length() > 1){
                        backRefNum = backRefNum.substring(0,backRefNum.length()-2);
                    }

                    i +=backRefNum.length()-1;
                    Pair<Integer> groupLength = groups.get(Integer.parseInt(backRefNum)-1);

                    minCounter += groupLength.x;
                    maxCounter = setMaxLength(maxCounter,groupLength.y);

                }
            }else if(re[i] == '|' ){
                char[] left = CharArrayUtil.subArray(re,0,i-1);
                char[] right = CharArrayUtil.subArray(re,i+1,re.length-1);

                Pair<Integer> leftSideLength = length(left);
                Pair<Integer> rightSideLength = length(right);

                int minLen = leftSideLength.x < rightSideLength.x ? leftSideLength.x : rightSideLength.x;
                int maxLen = leftSideLength.y > rightSideLength.y ? leftSideLength.y : rightSideLength.y;

                return new Pair(minLen,maxLen);

            }else if(re[i] == '[' ){//[a-z_\\[\\]
                int j=i;
                for (; re[j] != ']' ;j++){
                    if(re[j] == '\\' && re[j+1] == ']'){
                        j++;
                        continue;
                    }else if(re[j+1]=='-' && re[j+2] != ']'){
                        j+=2;
                    }
                }
                i=j;
                minCounter+=1;
                maxCounter = setMaxLength(maxCounter,1);
            }else if(re[i] == '{' ){
                Triplet<Integer> result = extractRange(re, i);
                i= result.z;
                minCounter+=result.x-1;
                maxCounter = setMaxLength(maxCounter,result.y -1);

            }else if(re[i] == '?'){
                minCounter-=1;

            }else if(re[i] == '*'){
                minCounter-=1;
                maxCounter=-1;
            }else if(re[i] == '+'){
                //do nothing
                maxCounter=-1;
            }else{
                minCounter+=1;
                maxCounter= setMaxLength(maxCounter,1);
            }
        }
        return new Pair(minCounter,maxCounter);
    }

    private static char[] extractGroup(char[] re, int index) {
        int startIndex = index+1;

        CharStack stack = new CharStack();
        while(true){
            if(re[index] == '('){
                stack.push('(');

            }else if(re[index] == ')'){
                stack.pop();
                if(stack.size() == 0) break;
            }
            index++;
        }

        return CharArrayUtil.subArray(re, startIndex,index-1);
    }

    /**
     *
     * @param re regular expression
     * @param i index
     * @return
     */
    private Triplet<Integer> extractRange(char[] re, int i){
        String min="";
        String max="";
        boolean afterComma = false;
        int j=i+1;
        for (; re[j] != '}' ;j++){
            if(re[j] == ','){
                afterComma = true;
                continue;
            }

            if(!afterComma){
                min+=re[j];
            }else {
                max+=re[j];
            }
        }
        if(max == "" && !afterComma) max = min;
        else if(max == "") max = "-1";
        return new Triplet<>(Integer.parseInt(min),Integer.parseInt(max),j);
    }

    private int setMaxLength(int oldLength, int newLength){
        if(oldLength < 0 || newLength < 0) return -1;
        else return oldLength+newLength;
    }
}
