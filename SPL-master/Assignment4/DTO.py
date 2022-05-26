class Hat:
    def __init__(self, id, topping, supplier, quantity):
        self.id = id
        self.topping = topping
        self.supplier = supplier
        self.quantity = quantity

    def __int__(self, hat_tuple):
        self.id = hat_tuple[0]
        self.topping = hat_tuple[2]
        self.supplier = hat_tuple[3]
        self.quantity = hat_tuple[4]


class Supplier:
    def __init__(self, id, name):
        self.id = id
        self.name = name

    def __int__(self, supplier_tuple):
        self.id = supplier_tuple[0]
        self.name = supplier_tuple[2]


class Order:
    def __init__(self, id, location, hat):
        self.id = id
        self.location = location
        self.hat = hat
