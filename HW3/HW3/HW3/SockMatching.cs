using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace HW3 {
    class SockMatching {
        private static readonly ConcurrentBag<Sock> bag = new ConcurrentBag<Sock>();

        public static void tmp(String[] args) {
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
            for (int i = 0; i < num; i++) {
                Sock sock = new Sock(color);
                bag.Add(sock);
                Console.WriteLine(color + "Sock: Produced " + i + " of " + num + " " + color + " socks.");
            }
        }

        public static void matchSocks() {
            List<Sock> sockList = bag.ToList();
            int redCount = 0;
            int blueCount = 0;
            int greenCount = 0;
            int orangeCount = 0;

            Dictionary<String, int> colorCounts = new Dictionary<String, int>();
            foreach(Sock sock in sockList) {
                switch(sock.color){
                    case "red": redCount++;
                        break;
                    case "blue": blueCount++;
                        break;
                    case "green": greenCount++;
                        break;
                    case "orange": orangeCount++;
                        break;
                }
            }

            colorCounts.Add("red", redCount);
            colorCounts.Add("blue", blueCount);
            colorCounts.Add("orange", orangeCount);
            colorCounts.Add("green", greenCount);

            foreach(var count in colorCounts) {
                for(int i = 0; i < count.Value/2; i++) {
                    sockList.Remove(new Sock(count.Key));
                    sockList.Remove(new Sock(count.Key));
                    Console.WriteLine("Matching thread: Found a pair of " + count.Key + " socks");
                    new Thread(() => washingMachine(new Sock(count.Key), new Sock(count.Key))).Start();
                }
            }
        }

        static void washingMachine(Sock sock1, Sock sock2) {
            Console.WriteLine("Washer Thread: Destroyed " + sock1.color + " Socks");
            sock1 = null;
            sock2 = null;
        }

        class Sock{
            public String color { get; set; }
            public Sock(String sockColor) {
                color = sockColor;
            }

        }
    
    }
}
