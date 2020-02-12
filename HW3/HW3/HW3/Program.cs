using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using System.Threading.Tasks;

namespace HW3 {
    class SockMatching {
        private static ConcurrentBag<Sock> bag = new ConcurrentBag<Sock>();

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
                bag.Add(sock);
                Console.WriteLine(sock.color + " sock is created!");
            }
        }



        public static void matchSocks() {
            Sock tmpSock = null;
            bag.TryTake(out tmpSock);
            int pairs = 0;
            foreach(Sock sock in bag) {
                if (sock.color.Equals(tmpSock.color)) {
                    Boolean foundPair = false;
                    int size = bag.Count;

                    while ((foundPair == false)) {
                        Sock tmp = null;
                        bag.TryTake(out tmp);

                        if (sock.color.Equals(tmp.color)) {
                            foundPair = true;
                        } else {
                            bag.Add(tmp);
                        }

                        size--;
                        //Console.WriteLine(pairs);
                    }
                    pairs++;
                }
            }

            Console.WriteLine(pairs);
        }
    }

    class Sock {
        public String color;
        public Sock(String sockColor) {
            color = sockColor;
        }
    }

}
