# Spectra

Spectra is a general purpose planning system. It extends STRIPS-style planning by allowing arbitrary DCEC and first-order formulae for state descriptions, background knowledge, and action descriptions rather than just predicates. This allows, for instance, handling domains with infinite or unbounded objects elegantly (among other things). 

[System Description Publication (2024)](https://rdcu.be/dIJ7F)  
[Overview Presentation (2017)](https://drive.google.com/open?id=1RHulFDgASACBkjvl-8ZEidj50NbGmKPu)

 * Drawbacks of propositional planning (current planning systems): 
 * **Expressivity**: Cannot express arbitrary constraints.  *“At every step make sure that no two blocks on the table have same color”*
   * **Domain Size**: Scaling to large domains of arbitrary sizes poses difficulty. 

## Example Problem Files

See the [example problems](https://github.com/RAIRLab/Spectra/tree/master/src/main/resources/org/rairlab/planner/problems)

## Installation

**Make sure you have Java 17 installed! While ShadowProver will, Spectra will not compile on Java 8.**

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

## Cite
```
﻿@article{Rozek2024,
  author={Rozek, Brandon and Bringsjord, Selmer},
  title={Spectra: An Expressive STRIPS-Inspired AI Planner Based on Automated Reasoning},
  journal={KI - K{\"u}nstliche Intelligenz},
  year={2024},
  month={May},
  day={22},
  issn={1610-1987},
  doi={10.1007/s13218-024-00847-8},
  url={https://doi.org/10.1007/s13218-024-00847-8}
}
```
