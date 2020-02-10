import java.util.LinkedList;
import java.util.Queue;

public class Recursions
{
    private static int headRecursionSum(int n)
    {
        if (n >= 1)
        {
            return headRecursionSum(n - 1) + n;
        }
        else
        {
            return n;
        }
    }

    private static int tailRecursionSum(int currentSum, int n)
    {
        if (n <= 1)
        {
            return currentSum + n;
        }
        else
        {
            return tailRecursionSum(currentSum + n, n - 1);
        }
    }

    private static int powersOfX(int n)
    {
        if (n == 0)
        {
            return 1;
        }
        else
        {
            return powersOfX(n - 1) * 10;
        }
    }

    private static int fibonacci(int n)
    {
        if (n <= 1)
        {
            return n;
        }
        else
        {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    private static String toBinary(int n)
    {
        if (n <= 1)
        {
            return String.valueOf(n);
        }
        else
        {
            return toBinary(n / 2) + String.valueOf(n % 2);
        }
    }

    public static void main(String[] args)
    {
        System.out.println("The sum of all numbers between 0 to 5: " + headRecursionSum(5));
        System.out.println("Same sum but with tail recursion: " + tailRecursionSum(15, 0));
        System.out.println("10 to the power of 3: " + powersOfX(3));
        System.out.println("Fibonaci Sequence up to 6: " + fibonacci(6));
        System.out.println("Converting 23 in decimal to binary: " + toBinary(23));
    }
}
