# Travelling Salesman as an NP-hard problem

From [Wikipedia](https://en.wikipedia.org/wiki/Travelling_salesman_problem):

> The **travelling salesman problem (TSP)** asks the following question: "Given a list of cities and the distances between each pair of cities, what is the **shortest possible route that visits each city and returns to the origin city**?" It is an NP-hard problem in combinatorial optimization, important in operations research and theoretical computer science.

## NP-hardness

A **decision problem** is a problem with a **yes** or **no** answer. **Computational complexity theory** studies the resources needed during computation to solve a given problem. **P** and **NP** are *complexity classes* in which a decision problem is categorized.

- P stands for "(deterministic) polynomial time".
  - Represents the set of all decision problems that can be **solved** in polynomial time.
- NP stands for "nondeterministic polynomial time".
  - Represents the set of all decision problems that can be **verified** in polynomial time.

A good example is the Sudoku game. Given a square to solve, coming up with a solution  for the square (solving) increases **exponentially** with the dimension of the square. But **verifying** if a solution is valid for that same square only takes polynomial time, based on the size of the square.

### P, NP, NP-Complete and NP-Hard

```text
____________________________________________________________
| Problem Type | Verifiable in P time | Solvable in P time | Increasing Difficulty
___________________________________________________________|           |
| P            |        Yes           |        Yes         |           |
| NP           |        Yes           |     Yes or No *    |           |
| NP-Complete  |        Yes           |      Unknown       |           |
| NP-Hard      |     Yes or No **     |      Unknown ***   |           |
____________________________________________________________           V
```

Notes on Yes or No entries:

- \* An NP problem that is also P is solvable in P time.
- ** An NP-Hard problem that is also NP-Complete is verifiable in P time.
- *** NP-Complete problems (all of which form a subset of NP-hard) might be. The rest of NP hard is not.

<img src="https://upload.wikimedia.org/wikipedia/commons/a/a0/P_np_np-complete_np-hard.svg" max-height="300" alt="NP Diagram"/>

### The P=NP problem

We know that P is a subet of NP, but we don't know if all NP problems can be **solved** in polynomial time. If it is proven that P is equal to NP, then all NP problems can be solved by a Turing Machine. As it stands, NP-hard problems can only be **solved** by non-deterministic TMs, that is, a Turing Machine that can be in more than one state at the same time (as if a computer had infinite threads).

#### Turing Machine

A Turing Machine is a theoretical computational model that defines an abstract machine. This machine is very simple in its working, but it defines what modern computers can do. As it is, the **strongest** program we can write in a digital computer **can be executed by a Turing Machine**.

A Turing Machine consists on:

- A **tape** divided into cells in which the machine may write. A cell can be written or empty.
- A **head** which moves along the tape, to the left or to the right, *one cell at a time* (it cannot jump more than one cell).
- A **state register** which keeps track of the state of the TM.
- A **finite table** of instructions which given the state in the state register (q) and the symbol that the head is reading in the tape (a), tells the machine to do the following **in sequence**:
    1. Either erase or write a symbol (on an empty cell or replacing the symbol that already was there).
    1. Move the head one cell to the left or to the right or keep it in the same cell.
    1. Keep on the same or change to a different state.

## Travelling Salesman and NP-hardness

The travelling salesman problem (TSP) is an NP-complete (and thus NP-hard) problem. The *worst-case running time* for any algorithm increases more than polynomially - **superpolynomially** - but no more than exponentially.

To get around this, when there is lots of data going into a TSP problem, the algorithms include an heuristics in order to decrease the solving time.