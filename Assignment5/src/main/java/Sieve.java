public class Sieve {

    public static void main(String args[]) {
        int n = 10000000;
        Long before = System.currentTimeMillis();
        Sieve g = new Sieve();
        g.sieve(n);
        System.out.println("\nTotal time: " + (System.currentTimeMillis() - before));
    }

    void sieve(int n) {
        boolean prime[] = new boolean[n + 1];
        for (int i = 0; i < n; i++)
            prime[i] = true;

        for (int p = 2; p * p <= n; p++) {
            if (prime[p] == true) {
                for (int i = p * p; i <= n; i += p)
                    prime[i] = false;
            }
        }

        for (int i = 2; i <= n; i++) {
            if (prime[i] == true)
                System.out.println(i + " ");
        }
    }


}

