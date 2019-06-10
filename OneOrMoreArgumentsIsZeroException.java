package rsa.exceptions;

public class OneOrMoreArgumentsIsZeroException extends EuclidException{
  public static void main(String[] args){

  }

  public OneOrMoreArgumentsIsZeroException(String s){
    super(s);
  }

  public OneOrMoreArgumentsIsZeroException(){
    super("");
  }
}
