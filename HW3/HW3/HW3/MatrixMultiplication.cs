using System;
using System.Threading;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading.Tasks;

class Program {
    static int matrixARow;
    static int matrixBRow;
    static int matrixAColumn;
    static int matrixBColumn;
    static int[,] matrixA ;
    static int[,] matrixB;
    static int[,] result;
    static Thread[] threadPool;

    static void runMultiplication(int index) {
        for (int i = 0; i < matrixARow; i++) {
            for (int j = 0; j < matrixBColumn; j++) {
                result[index, i] += matrixA[index, j] * matrixB[j, i];
            }
        }
    }

    static void fillMatrix(int n, int m, int k) {
        matrixA = new int[n, m];
        matrixB = new int[m, k];
        result = new int[n, m];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrixA[i, j] = rand.Next(0, 5);
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                matrixB[i, j] = rand.Next(0, 5);
            }
        }
        printMatrix(matrixA);
        Console.Write(Environment.NewLine);
        printMatrix(matrixB);
        Console.Write(Environment.NewLine);


    }

    static void multiplyMatrices(int numThreads) {
        threadPool = new Thread[numThreads];
        
        for (int i = 0; i < matrixARow; i++) {
            int k = i;
            threadPool[k] = new Thread(() => runMultiplication(k));
            threadPool[k].Start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threadPool[i].Join();
            } catch (Exception e) {
                Console.WriteLine(e.Message);
            }
        }
    }

    static void printMatrix(int[,] matrix) {
        for (int i = 0; i < matrix.GetLength(0); i++) {
            for (int j = 0; j < matrix.GetLength(0); j++) {
                Console.Write(string.Format("{0} ", matrix[i, j]));
            }
            Console.Write(Environment.NewLine);
        }
    }

    static void Main(String[] args) {
        Console.Write("n=");
        int n = Convert.ToInt32(Console.ReadLine());
        matrixARow = n;
        Console.Write("m=");
        int m = Convert.ToInt32(Console.ReadLine());
        matrixBColumn = m;
        matrixAColumn = m;
        Console.Write("k=");
        int k = Convert.ToInt32(Console.ReadLine());
        matrixBRow = k;
        Console.Write("numThreads=");
        int numThreads = Convert.ToInt32(Console.ReadLine());
        fillMatrix(n, m, k);
        multiplyMatrices(numThreads);
        printMatrix(result);
    }
}