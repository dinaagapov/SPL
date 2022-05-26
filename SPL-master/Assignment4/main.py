import sys

from DTO import Hat, Supplier, Order
from Repository import _Repository, repo


def main(args):
    repo.init()
    repo.create_tables()
    repo._commit()
    #configFile = ("config.txt")
    configFile = args[1]
    with open(configFile) as inputfile:
        inputfile.readline()
        for line in inputfile:
            line = line.split("\n")[0]
            if len(line.split(",")) == 4:
                repo.hats.insert(Hat(line.split(',')[0], line.split(',')[1], line.split(',')[2], line.split(',')[3]))
                repo._commit()
            else:
                repo.supplier.insert(Supplier(line.split(',')[0], line.split(',')[1]))
                repo._commit()
    #configFile = ("orders.txt")
    configFile = args[2]
    index = 1
    output = open(args[3], "w")
    with open(configFile) as inputfile2:
        for line in inputfile2:
            line = line.split("\n")[0]
            loc, top = line.split(',')[0], line.split(',')[1]
            hat = repo.hats.find(top)
            sup = repo.supplier.find(hat.supplier)
            repo.orders.insert(Order(index, loc, hat.id))
            repo._commit()
            index += 1
            output.write(top + "," + sup.name + "," + loc + "\n")
            if hat.quantity > 0:
                repo.hats.update(hat.id, hat.quantity)
                repo._commit()
            if hat.quantity-1 == 0:
                repo.hats.delete(hat.id)
                repo._commit()
        output.close()


if __name__ == '__main__':
    main(sys.argv)
