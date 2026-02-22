package io.github.nmaksimka.filterfiles;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.exit(new FilterFiles(System.out, System.err).run(args));
    }
}
