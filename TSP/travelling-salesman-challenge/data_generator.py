import random
import string

def random_cities(n_cities):
    cities = set()
    while len(cities) < n_cities:
        city = "".join(random.choice(string.ascii_uppercase) for __ in range(3))
        cities.add(city)
    return sorted(list(cities))

N_CITIES = 10
cities = random_cities(N_CITIES)

print(random.choice(cities))
for src in cities:
    for dst in cities:
        if src == dst:
            continue
        for day in range(N_CITIES):
            if (random.random() > 0.1): # 10% of not having this flight. Use this in order to "limit" some routes.
                print("{} {} {} {}".format(src, dst, day, random.randint(10, 1500)))
