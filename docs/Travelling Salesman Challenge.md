# Travelling Salesman Challenge

## Assignment

Since Kiwi.com has all the worlds' airline schedules in one database, we can find more than just flights from A to B.
Our search engine can also respond to interesting search requests, like from A to anywhere.
We also offer MultiCity requests, like “I want to go from Brno to Palermo, then to Moscow, and back to Brno”.

An example of MultiCity requests (without specifying time intervals).

This may seem strange, but we sell hundreds of these flights per day.
Another surprising fact is that in a number of cases, passengers don't care about the order in which they visit these cities.
Imagine how this can help an elderly Japanese tourist who just wants to visit the five biggest cities in Europe.
Finding flight connections without caring about the order will enable us to combine hotels to create a "Holiday Plan".
If it's such a great market fit, why isn't every flight search engine doing this?

The issue is, skipping the ordering requirement makes the problem hard. It's an 80+ year-old NP-hard problem.

We love hard problems, and we also want to share the joy of this challenge.
That’s why we've decided to work on it with the wider part of the tech and academic community.

We're rewarding you with flight vouchers!

## Task

The input data is a graph defined in CSV with data like this:

```text
<City to start from>  # always a single IATA code
<FROM> <TO> <DateOfDeparture> <PRICE>
<FROM> <TO> <DateOfDeparture> <PRICE>
```

The task will be to find a cycle with the lowest possible price for the given N cities.
The difference between this and the classic Travelling Salesman problem is that we specify the day when the flight and the price apply.
The more cities your algorithm can generate with a valid cycle, the more points you get. In general, it's simple. But wait for final rules.

Process and evaluation

There will be two phases: development and evaluation. The development phase will take two weeks.

## Is this the classic Travelling Salesman problem?

This is a more realistic, time-dependent variant of it. It means that flights are available only on given days.

- All flights are immediate, they take no time.
- On each day you have to board 1 and only 1 flight.

