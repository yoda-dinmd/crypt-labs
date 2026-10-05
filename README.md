# Cryptography laboratories

Java 17+ Maven project. Open the root `pom.xml` in IntelliJ IDEA and select a **JDK 17 or newer** for both the project SDK and Maven runner. IntelliJ can use its bundled Maven; a separate Maven installation is optional. No IDE project files are committed.

## Build and run

From the repository root:

```sh
mvn package
java -jar lab1/target/lab1-1.0-SNAPSHOT.jar
```

Run tests only with `mvn test`. In IntelliJ, run `md.utm.cryptography.lab1.Main` directly, or run the parent project's Maven `package` lifecycle.

`lab1` implements both Romanian Caesar cipher tasks: the standard 31-letter alphabet and a keyword permutation. The numeric shift must be 1–30. Keywords require at least seven letters, with no spaces. Messages accept Romanian letters and ordinary spaces; output is uppercase without spaces. Romanian cedilla variants and decomposed diacritics are normalized. Invalid inputs are explained and requested again. Choose `0` in the cipher menu to exit.

## Add another laboratory

Create `lab2/pom.xml` inheriting the root parent, use `lab2` as its artifact ID, add its source directories, and add `<module>lab2</module>` to the root POM. Repeat for `lab3` and later modules. Only `lab1` exists now.

Build output, LaTeX temporary files, local IDE settings and the original template directory are excluded through `.gitignore`. Console evidence and template dependencies are kept in Git.
