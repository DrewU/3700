//using System;
//using System.Threading;
//using System.Collections.Generic;

//namespace MatrixMultiplication {
//    class Program {
//        static void tmp(string[] args) {
//            Console.Write("n=");
//            int n = Convert.ToInt32(Console.ReadLine());
//            Console.Write("m=");
//            int m = Convert.ToInt32(Console.ReadLine());
//            Console.Write("k=");
//            int k = Convert.ToInt32(Console.ReadLine());
//            Matrix A = new Matrix(n, m).GenerateRandomMatrix();
//            Matrix B = new Matrix(m, k).GenerateRandomMatrix();
//            A.Print();
//            Console.WriteLine(new String('-', 20));
//            B.Print();
//            Console.WriteLine(new String('-', 20));
//            Matrix C = A * B;
//            C.Print();
//            Console.ReadLine();
//        }
//    }

//    class Matrix {
//        public int rowSize;
//        public int columnSize;
//        double[,] arr;
//        public static Mutex mutex = new Mutex();

//        Matrix() { }
//        public Matrix(int row, int column) {
//            this.rowSize = row;
//            this.columnSize = column;
//            arr = new double[row, column];
//        }
//        public double[] GetColumn(int i) {
//            double[] res = new double[rowSize];
//            for (int j = 0; j < rowSize; j++)
//                res[j] = arr[j, i];
//            return res;
//        }
//        public double[] GetRow(int i) {
//            double[] res = new double[columnSize];
//            for (int j = 0; j < columnSize; j++)
//                res[j] = arr[i, j];
//            return res;
//        }
//        public double this[int i, int j] {
//            get { return arr[i, j]; }
//            set { arr[i, j] = value; }
//        }
//        public Matrix GenerateRandomMatrix() {
//            Random rnd = new Random();
//            for (int i = 0; i < rowSize; i++)
//                for (int j = 0; j < columnSize; j++)
//                    arr[i, j] = rnd.Next(10);
//            return this;
//        }

//        public void Print() {
//            for (int i = 0; i < rowSize; i++) {
//                for (int j = 0; j < columnSize; j++)
//                    Console.Write(arr[i, j] + " ");
//                Console.WriteLine();
//            }
//        }

//        public static Matrix operator *(Matrix a, Matrix b) {
//            Matrix result = new Matrix(a.rowSize, b.columnSize);
//            List<Thread> threads = new List<Thread>();
//            for (int i = 0; i < a.rowSize; i++)
//                for (int j = 0; j < b.columnSize; j++) {
//                    int tempi = i;
//                    int tempj = j;
//                    Thread thread = new Thread(() => VectorMult(tempi, tempj, a, b, result));
//                    thread.Start();
//                    threads.Add(thread);
//                }
//            foreach (Thread t in threads)
//                t.Join();
//            return result;
//        }

//        public static void VectorMult(int tmpi, int tmpj, Matrix a, Matrix b, Matrix result) {
//            mutex.WaitOne();
//            int i = tmpi;
//            int j = tmpj;
//            double[] x = a.GetRow(i);
//            double[] y = b.GetColumn(j);

//            for (int k = 0; k < x.Length; k++)
//                result[i, j] += x[k] * y[k];

//            mutex.ReleaseMutex();
//        }
//    }


//}




using System;
using System.Threading;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading.Tasks;

class Program {
    static int rows = 3;
    static int columns = 3;
    static int[] temp = new int[rows * columns];
    static int[,] matrixA = new int[rows, columns];
    static int[,] matrixB = new int[rows, columns];
    static int[,] result = new int[rows, columns];
    static Thread[] threadPool;

    static void runMultiplication(int index) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[index, i] += matrixA[index, j] * matrixB[j, i];
            }
        }
    }

    static void fillMatrix(int n, int m, int k) {


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
        
        for (int i = 0; i < rows; i++) {
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
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Console.Write(string.Format("{0} ", matrix[i, j]));
            }
            Console.Write(Environment.NewLine);
        }
    }

    static void Main(String[] args) {
        Console.Write("n=");
        int n = Convert.ToInt32(Console.ReadLine());
        Console.Write("m=");
        int m = Convert.ToInt32(Console.ReadLine());
        Console.Write("k=");
        int k = Convert.ToInt32(Console.ReadLine());
        Console.Write("numThreads=");
        int numThreads = Convert.ToInt32(Console.ReadLine());
        fillMatrix(n, m, k);
        multiplyMatrices(numThreads);
        printMatrix(result);
    }
}