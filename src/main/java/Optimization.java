import java.text.DecimalFormat;

/**
 * Created by suchintan on 2014-09-24.
 */
public class Optimization {
    public static DecimalFormat d = new DecimalFormat("####.####");
    public static int c = 0;

    public static void main(String[] args) {
        double r = -3;
        double error = 0.101;


        double bk = 4.0/3;
        double bk1 = -4.0;
        double ak = -4.0;


        do {
            c++;

            System.out.println("\nIteration " + c + ": bk = " + d.format(bk) + ", ak = " + d.format(ak) + ", bk-1 = " + d.format(bk1));
            double s;

            System.out.println("f(b_" +  (c-1) + ") = f(" + d.format(bk) + ") = " + d.format(f(bk)) + "\\\\ f(a_ " + (c-1) + ") = f(" + d.format(ak) + ") = " + d.format(f(ak)) + "\\\\ f(b_" + (c-2) + ") = f(" + d.format(bk1) + ") = " + d.format(f(bk1)));
            if (f(bk) != f(ak) && f(bk1) != f(ak) && f(bk) != f(bk1)) {
                s = iqi(bk, bk1, ak);
            } else {
                s = secant(bk, ak);
            }

            double bknext = 0.0;
            if (bounds(ak, bk, bk1, c, s)) {
                bknext = s;
            } else {
                bknext = midpoint(bk, ak);
            }

            System.out.println("b_" + c + " = " + d.format(bknext));
            double aknext = anext(ak, bknext, bk);

            boolean swap = Math.abs(f(bknext)) > Math.abs(f(aknext));
            System.out.println("|f(b_{" + c + "})| > |f(a_{" + c + "})| = |" + d.format(f(bknext)) + "|" + (swap ? ">" : "\\ngtr") +  "|" + d.format(f(aknext)) + "|");
            if (swap) {
                double tmp = bknext;
                bknext = aknext;
                aknext = tmp;
            }

            bk1 = bk;
            bk = bknext;
            ak = aknext;

        }while(error(bk, r) > error);
    }

    public static double f(double x){
        return (x+3) * (x-1) * (x-1) * 1.0;
    }

    public static double midpoint(double bk, double ak){
        double m = (bk + ak) / 2.0;
        System.out.println("Midpoint: " + m);
        System.out.println("m = \\frac{a_" + (c-1) + "+ b_" + (c-1) + "}{2} = \\frac{"  + d.format(ak) +  " + " + d.format(bk) + "}{2} = " + d.format(m) );
        return m;
    }

    public static double secant(double bk, double ak){
        double s = bk - ((bk - ak) * f(bk) / (f(bk) - f(ak)));
        System.out.println("Secant: " + s);
        System.out.println("s = b_" + (c-1) + "- \\frac{b_" + (c-1) + " - a_{" + (c-1) + "}}{f(b_" + (c-1) + ") - f(a_{" + (c-1) + "})}f(b_" + (c-1) + ") = " + d.format(bk) + "- \\frac{(" + d.format(bk) + ") - (" + d.format(ak) + ")}{(" + d.format(f(bk)) + ") - (" + d.format(f(ak)) + ")}(" + d.format(f(bk)) + ") = " + d.format(s));
        return s;
    }

    public static double iqi(double bk, double bk1, double ak){
        double s = lagrange(bk, bk1, ak) + lagrange(bk1, bk, ak) + lagrange(ak, bk1, bk);
        System.out.println("IQI: " + s);

        String l = "b_{" + (c-2) + "}\\frac{f(b_{" + (c-1) + "})f(a_" + (c-1) + ")}{(f(b_{" + (c-2) + "}) - f(b_{" + (c-1) + "}))(f(b_{" + (c-2) + "})-f(a_" + (c-1) + "))} + \\\\";
        String m = "b_{" + (c-1) + "}\\frac{f(b_{" + (c-2) + "})f(a_" + (c-1) + ")}{(f(b_{" + (c-1) + "}) - f(b_{" + (c-2) + "}))(f(b_{" + (c-1) + "})-f(a_" + (c-1) + "))} + \\\\";
        String n = "a_{" + (c-1) + "}\\frac{f(b_{" + (c-2) + "})f(b_" + (c-1) + ")}{(f(a_{" + (c-1) + "}) - f(b_{" + (c-2) + "}))(f(a_{" + (c-1) + "})-f(b_" + (c-1) + "))}";
        System.out.println("s = " + l + m + n);

        System.out.println("s = " + lagrangetext(bk1, bk, ak) + " \\\\+ " + lagrangetext(bk, bk1, ak) + "\\\\ +" + lagrangetext(ak, bk1, bk));
        System.out.println("s = " + d.format(s));
        return s;
    }

    public static double lagrange(double bk, double bk1, double ak){
        return bk * (f(bk1) * f(ak)) / ((f(bk) - f(bk1)) * (f(bk) - f(ak)));
    }

    public static String lagrangetext(double bk, double bk1, double ak){
        String s = d.format(bk) + "\\frac{(" + d.format(f(bk1)) + ")(" + d.format(f(ak)) + ")}{((" + d.format(f(bk)) + ") - (" + d.format(f(bk1)) + "))((" + d.format(f(bk)) + ") - (" + d.format(f(ak)) + "))}";
        return s;//bk * (f(bk1) * f(ak)) / ((f(bk) - f(bk1)) * (f(bk) - f(ak)));
    }

    public static boolean bounds(double ak, double bk, double bk1, int c, double s){
        boolean first = s <  bk && s > ((3 * ak + bk) / 4);
        System.out.println("First bounds check: " + first);
        System.out.println("s \\ \\epsilon \\ [\\frac{3a_" + (c - 1) + "+ b_" + (c - 1) + "}{4}, b_" + (c - 1) + "]");
        System.out.println(d.format(s) + (first ? "~\\epsilon" : "~\\widetilde{\\epsilon}") + " \\ [" + d.format((3 * ak + bk) / 4.0) + ", " + d.format(bk) + "]");
        boolean second = Math.abs(s - bk) <= 0.5 * Math.abs(bk - bk1);
        System.out.println("Second bounds check: " + second);
        System.out.println("|s-b_" + (c-1) + "| \\leq \\frac{1}{2} |b_" + (c-1) + " - b_{" + (c-2) + "}|");
        System.out.println("|(" + d.format(s) + ") - (" + d.format(bk) + ")| \\leq \\frac{1}{2} |(" + d.format(bk) + ") - (" + d.format(bk1) + ")|");

        return first && second;
    }

    public static double anext(double ak, double bknext, double bk){
        System.out.print("a_{" + c + "} = \\left\\{\\begin{matrix}" +
                "a_" + (c - 1) + "\\ if \\ f(a_" + (c - 1) + ")f(b_{" + (c) + "}) < 0 \\\\" +
                "b_" + (c - 1) + "\\ otherwise" +
                "\\end{matrix}\\right.");

        System.out.print("= \\left\\{\\begin{matrix}" +
                d.format(ak) + "\\ if \\ (" + d.format(f(ak)) + ")(" + d.format(f(bknext)) + ") < 0 \\\\" +
                d.format(bk) + "\\ otherwise" +
                "\\end{matrix}\\right. = ");

        double anext = 0.0;
        if(f(ak) * f(bknext) < 0){
            anext = ak;
            System.out.println(d.format(anext));
            System.out.println("Mean Value theorem satisfied! Use old ak");
        }else{
            anext = bk;
            System.out.println(d.format(anext));
            System.out.println("Failed MVT use bk");
        }
        return anext;
    }

    public static double error(double rk, double r){
        double error = Math.abs((r - rk) / rk);
        System.out.println("Error: " + error);
        System.out.println("|\\frac{" + d.format(r) + " - " + d.format(rk) + "}{" + d.format(rk) + "}| = " + d.format(error) + (error < 0.10 ? "\\leq" : ">") + "10\\% ");
        return error;
    }
}
