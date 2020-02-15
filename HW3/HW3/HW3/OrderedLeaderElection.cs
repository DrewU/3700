using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Text;
using System.Threading;

namespace HW3 {
    class OrderedLeaderElection {
        public const int min = Int32.MinValue;
        public const int max = Int32.MaxValue;
        public static ConcurrentDictionary<int, Thread> threadCollection = new ConcurrentDictionary<int, Thread>();
        public static int maxRank = max;
        public static void Main(String[] args) {

            generateThreads(5);

        }

        public static void generateThreads(int numThreads) {
            for(int i = 0; i < numThreads; i++) {
                Random rand = new Random();
                StayAwake stayAwake = new StayAwake();
                Thread rankThread = new Thread(new ThreadStart(stayAwake.ThreadMethod));
                rankThread.Start();


                ElectedOfficial official1 = new ElectedOfficial();
                official1.rank = rand.Next(min, max);
                official1.leader = "Thread " + i;
                Thread thread1 = new Thread(new ThreadStart(official1.value));
                threadCollection.TryAdd(official1.rank, thread1);
                thread1.Name = ("Thread " + i);
                thread1.Start();
                rankThread.Interrupt();
                rankThread.Join();
            }
        }
        class StayAwake {
            bool sleepSwitch = false;

            public StayAwake() { }
            public void ThreadMethod() {
                try {
                    
                    Thread.Sleep(Timeout.Infinite);
                } catch (ThreadInterruptedException e) {

                        foreach (var thread in threadCollection) {
                            thread.Value.Interrupt();
                            thread.Value.Join();
                        }
                    
                }



            }
        }
        class ElectedOfficial {
            public int rank { get; set; }
            public String leader { get; set; }

            public ElectedOfficial() { }
            public void value() {
                Thread thr = Thread.CurrentThread;
                Console.Error.WriteLine("The name of the current thread is: " + thr.Name + ", the rank is " + rank + " and its leader is " + leader);
                try {
                    Thread.Sleep(Timeout.Infinite);
                } catch (ThreadInterruptedException e) {
                    int currentMaxRank = min;
                    foreach (var thread in threadCollection) {
                        if (thread.Key > currentMaxRank) {
                            currentMaxRank = thread.Key;
                        }
                    }
                    Thread tmp = null;
                    threadCollection.TryGetValue(currentMaxRank, out tmp);
                    leader = tmp.Name;
                    Console.Error.WriteLine("The name of the current thread is: " + thr.Name + ", the rank is " + rank + " and its new leader is " + leader);
                }
            }
        }
    }




}




