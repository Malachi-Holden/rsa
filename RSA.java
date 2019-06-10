package rsa;

import rsa.exceptions.*;
import java.math.*; //BigInteger
import java.util.*; //Random


public class RSA{
  /**
    class for rsa encryption. Mainly it generates the keys. Use the
    accompanying encryption class for actually performing encryption

    Initialization:
    RSA R = new RSA(byteLength);
    // byteLength should be at least half of the number of bits of your smallest message
    // so if you plan to encrypt the message 64 (the number, not the string '64'),
    // use a bit length of at least 3.
    //
    R.make_keys();
    //now (R.priv, R.mod) is the private key pair and
    // (R.pub, R.mod) is the public key pair

  **/

  //--------------------------
  //testing methods:

  public static void main(String[] args)
  {
    System.out.println(necessaryBits(new BigInteger("1234567890")));
  }

  public static boolean cryptDoesWork(RSA R, BigInteger message){
    BigInteger encrypted = message.modPow(R.pub, R.mod);
    BigInteger decrypted = encrypted.modPow(R.priv, R.mod);
    return decrypted.equals(message);
  }

  public static int necessaryBits(BigInteger message){
    int bits = 2;
    RSA R = new RSA(2);
    R.makeKeys();
    while (!cryptDoesWork(R, message)){
      bits++;
      R.setBitLength(bits);
      R.makeKeys();
    }
    return bits;
  }


//--------------------------------------


  public BigInteger fac1, fac2, mod, pub, priv, max;
  public static final BigInteger Z = BigInteger.ZERO; // 0
  public static final BigInteger N = BigInteger.ONE; // 1
  public static final BigInteger NN = BigInteger.ONE.negate(); // -1

  Random rnd;
  public int byteLength;

  public RSA(){
    this(2);
  }

  public RSA(int _byteLength /*must be at least 2*/){
    this(_byteLength, new Random()); //uses standard java random object. Use a more
    //secure random engine for sensitive encryption
  }

  public RSA(int _byteLength/*must be at least 2*/, Random _rnd){
    this.rnd = _rnd;
    this.byteLength = _byteLength;
  }

  public void makeKeys(){
    fac1 = BigInteger.probablePrime(this.byteLength, this.rnd);
    fac2 = this.fac1.nextProbablePrime();
    pub = this.fac2.nextProbablePrime();
    try{
      makeKeys(fac1, fac2, pub);
    }
    catch(EqualPrimesException e){
      System.out.println("Equal primes. Something must be wrong with BigInteger.");
    }
  }

  public void makeKeys(BigInteger fac1, BigInteger fac2,
      BigInteger pub) throws EqualPrimesException{
    /**
      generates the public and private keys.
    **/
    setPrimes(fac1, fac2, pub);
    this.mod = this.fac1.multiply(this.fac2);
    BigInteger euler = (this.fac1.subtract(this.N)).multiply(
      this.fac2.subtract(this.N)
      ); // (this.fac1-1)*(this.fac2-1)
    tuple sol = new tuple();
    while (true){
      try{
        sol = diophantine_nx(euler, this.pub);
        break;
      }
      catch(EuclidException e){

        setPrimes(fac1, fac2, pub);
        this.mod = this.fac1.multiply(this.fac2);
        euler = (this.fac1.subtract(this.N)).multiply(
          this.fac2.subtract(this.N)
          ); // (this.fac1-1)*(this.fac2-1)
      }
    }
    BigInteger balance = sol.x.negate();
    this.priv = (balance.multiply(euler.add(this.N))).divide(this.pub);
  }

  public void setPrimes(BigInteger _fac1, BigInteger _fac2,
      BigInteger _pub) throws EqualPrimesException{
        /**
          Checks if the given numbers are equal. If any
          two are equal, throws it out
        **/
    boolean isEqual = false;
    if (_fac1.equals(_fac2)){
      isEqual = true;
    }else if (_fac2.equals(_pub)){
      isEqual = true;
    }else if (_fac1.equals(_pub)){
      isEqual = true;
    }

    if (isEqual){
      throw new EqualPrimesException("Two or more of the given primes are equal.");
    }else{
      fac1 = _fac1;
      fac2 = _fac2;
      pub = _pub;
    }
  }

  public void setBitLength(int _byteLength){
    this.byteLength = _byteLength;
  }

  public static tuple diophantine(BigInteger a, BigInteger b) throws EuclidException{
    /**
      Inputs a and b are the coefficients a and b in the equation
      a*x+b*y==1
      diophantine(a,b) returns a tuple (x, y), where x and y are integer
      solutions to the above equation (all inputs and outputs are of long type)
    **/
    if ((a.equals(Z))||(b.equals(Z))||(a.remainder(b).equals(Z))){
      throw new OneOrMoreArgumentsIsZeroException("either a or b is 0");
    }
    BigInteger r = a.remainder(b);
    BigInteger k = a.divide(b);
    if (b.remainder(r).equals(Z)){
      if (r.equals(N)){
        return new tuple(N, k.negate());
      }
      else if (r.equals(NN)){
        return new tuple(NN, k);
      }else{
        throw new ArgumentsNotCoprimeException("a and b must be coprime (gcd(a,b)==1)");
      }
    }
    else {
      tuple pre = diophantine(b,r);
      return(new tuple(pre.y, pre.x.subtract(k.multiply(pre.y))));
    }
  }

  public static tuple diophantine_nx (BigInteger a, BigInteger b) throws EuclidException{
    /**
    Inputs a and b are the coefficients a and b in the equation
    a*x+b*y==1
    diophantine(a,b) returns a tuple (x, y), where x and y are
    non negative integer solutions to the above equation
    (all inputs and outputs are of long type)
    **/
    tuple pre = diophantine(a, b);
    BigInteger x = pre.x;
    BigInteger y = pre.y;
    if (x.compareTo(Z)==1){
      BigInteger k = x.negate().divide(b);
      if (b.compareTo(Z) == 1){
        x = x.add(k.subtract(N).multiply(b));
        y = y.subtract(k.subtract(N).multiply(a));
      }
      else{
        x = x.add(k.add(N).multiply(b));
        y = y.subtract(k.add(N).multiply(a));
      }
    }
    return new tuple(x,y);
  }

}

class tuple{
  public BigInteger x,y;
  public tuple(BigInteger x, BigInteger y){
    this.x = x;
    this.y = y;
  }

  public tuple(){
    this.x = BigInteger.ZERO;
    this.y = BigInteger.ZERO;
  }

}
