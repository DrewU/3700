using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using System.Threading.Tasks;

namespace HW3 {
    class SockMatching {
        private static ConcurrentQueue<Sock> queue = new ConcurrentQueue<Sock>();

        public static void Main(String[] args) {
            string[] colors = { "red", "blue", "green", "orange" };
            List<Task> tasks = new List<Task>();
          
            foreach (var color in colors) {
                Random random = new Random();
                Task t = Task.Run(() => createSocks(color, random.Next(1, 100)));
                tasks.Add(t);
            }
            Task.WaitAll(tasks.ToArray());

            tasks.Clear();
            Task tmp = Task.Run(() => matchSocks());
            tasks.Add(tmp);
            Task.WaitAll(tasks.ToArray());

        }

        public static void createSocks(String color, int num) {
            for(int i = 0; i < num; i++) {
                Sock sock = new Sock(color);
                queue.Enqueue(sock);
                Console.WriteLine(sock.color + " sock is created!");
            }
        }



        public static void matchSocks() {
            int k = 0;
            int z = queue.Count;
            for (int i = 0; i < z; i++) {
                Boolean foundPair = false;
                Sock mySock = null;
                queue.TryDequeue(out mySock);
                foreach (Sock sock in queue) {
                    if (sock.color.Equals(mySock.color)) {
                        k++;
                        Console.WriteLine("Found pair " + k);
                        foundPair = true;
                    }
                }

                if (!foundPair) {
                    queue.Enqueue(mySock);
                }
            }
        }
    }

    class Sock {
        public String color;
        public Sock(String sockColor) {
            color = sockColor;
        }
    }

}
