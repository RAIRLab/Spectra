# Spectra

Spectra is a general purpose planning system. It extends STRIPS-style planning by allowing arbitrary DCEC and first-order formulae for state descriptions, background knowledge, and action descriptions rather than just predicates. This allows, for instance, handling domains with infinite or unbounded objects elegantly (among other things). 

[Overview Presentation (pdf)](https://drive.google.com/open?id=1RHulFDgASACBkjvl-8ZEidj50NbGmKPu)

 * Drawbacks of propositional planning (current planning systems): 
 * **Expressivity**: Cannot express arbitrary constraints.  *“At every step make sure that no two blocks on the table have same color”*
   * **Domain Size**: Scaling to large domains of arbitrary sizes poses difficulty. 


## Installation

First, we need to make sure ShadowProver is installed.

```bash
git clone --recursive https://github.com/RAIRLab/ShadowProver.git
```

```bash
cd ShadowProver
mvn package
mvn install
```

Now, we can clone the Spectra repository.

```bash
git clone --recursive https://github.com/RAIRLab/Spectra.git
```

Similarly build and install the java project

```bash
cd Spectra
mvn package
mvn install
```

Now you should be able to run Spectra:

```bash
./run_spectra.sh [problem_file_path]
```



## Spectra's Architecture ##

 
![spectra-arch.png](https://bitbucket.org/repo/Mjq4bX/images/2495888298-spectra-arch.png)

## Example Input File ##

![examples.png](https://bitbucket.org/repo/Mjq4bX/images/3136509575-examples.png)

