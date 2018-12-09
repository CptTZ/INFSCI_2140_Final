# INFSCI 2140 final project - Yelp

{lez36,mel165,yiz141}#pitt.edu

## Build & Start

### Dependency

#### System

- Windows: Has Visual Studio C++ build tools available
- Linux: gcc or any other C compiler

#### Java

- OpenJDK 1.8, and have `java`, `javac` added to your `PATH` variable.
- Maven manages the rest of the dependencies

#### Python (NLP)

Suggest using Anaconda to manage dependencies, suppose you changed current directory to the root of our source.

```bash
conda create -n ir python=3.7 spacy=2.0.12 nltk pandas
conda activate ir
# Below is not necessary unless start from scratch
pip install https://github.com/huggingface/neuralcoref-models/releases/download/en_coref_md-3.0.0/en_coref_md-3.0.0.tar.gz
python scripts/IR-initSentiRank.py
```

### How to

- Build

Skip the unit test to speed up. Command as follows:

```shell
./mvnw clean package -DskipTests=true
```

If you want to run tests, be sure to put `pgh_review.csv` in appropriate position.

- Start

```shell
java -jar target/finalprj-0.0.1.jar
```

Then goto any web browser, view <http://127.0.0.1:10088>.
