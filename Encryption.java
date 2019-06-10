package rsa;
import rsa.exceptions.*;
import java.math.*; //BigInteger
import java.util.*; //Random
import java.io.*;

public class Encryption extends RSA{
  public static void main(String[] args){
    BigInteger message;
    if (args.length > 0)
    {
      message = new BigInteger(args[0]);
    }
    else
    {
      System.out.println("Using \"12345\" as message");
      message = new BigInteger("12345");
    }
    Encryption E = new Encryption(15);
    System.out.print("Encrypted message: ");
    BigInteger en = E.encrypt(message);
    System.out.println(en);
    System.out.print("Decrypted message: ");
    System.out.println(E.decrypt(en));
  }

  public static final BigInteger T = new BigInteger("2");

  public Encryption(){
    this(2);
  }

  public Encryption(int _byteLength /*must be at least 2*/){
    this(_byteLength, new Random());
  }

  public Encryption(int _byteLength/*must be at least 2*/, Random _rnd){
    super(_byteLength, _rnd);
    makeKeys();
  }

  public Encryption(int _byteLength/*must be at least 2*/, Random _rnd,
  BigInteger f1, BigInteger f2, BigInteger pb) throws EqualPrimesException{
    super(_byteLength, _rnd);
    makeKeys(f1, f2, pb);
  }

  public BigInteger encrypt(BigInteger message){
    int bits = intLog(message, T);
    if (2*byteLength <= bits){
      Integer BITS = new Integer(bits);
      Integer BITLENGTH = new Integer(byteLength);
      System.out.println("byteLength is too small to reliably encrypt or decrypt.");
      System.out.println("byteLength should be at least half the bit length of the message.");
      String error = "message size (bits): " + BITS.toString()
        +"\nbyteLength: "+ BITLENGTH.toString();
      System.out.println(error);
    }
    return message.modPow(pub, mod);
  }

  public BigInteger decrypt(BigInteger message){

    return message.modPow(priv, mod);
  }

  public static int intLog(BigInteger x, BigInteger base){
    int log = 0;
    x = x.divide(base);
    while (x.compareTo(Z)==1){
      log ++;
      x = x.divide(base);
    }
    return log;
  }


//end of class Encryption
}





//_____________
