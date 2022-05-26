from DTO import Hat, Supplier, Order


class _Hats:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, hat):
        self._conn.execute("""
               INSERT INTO hats (id, topping, supplier, quantity) VALUES (?, ? , ? , ?)
           """, [hat.id, hat.topping, hat.supplier, hat.quantity])

    def find(self, topping):
        c = self._conn.cursor()
        c.execute("""
        select * from hats where topping = ? and supplier = (
        select min(supplier)
            from(
            select supplier
            from hats
            where topping = ?)
        )
        """, [topping,topping, ])
        return Hat(*c.fetchone())

    #def find(self, topping):
        #c = self._conn.cursor()
        #c.execute("""
        #    SELECT * FROM hats WHERE topping = ?
        #""", [topping, ])
        #return Hat(*c.fetchone())

    def delete(self, hat_id):
        self._conn.execute("""
               DELETE FROM hats where id = (?)
           """, [hat_id, ])

    def update(self, hat_id, hat_quantity):
        self._conn.execute("""
               UPDATE hats SET quantity = (?) WHERE id = (?)
           """, [hat_quantity - 1, hat_id])


class _Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
               INSERT INTO suppliers (id, name) VALUES (?, ?)
           """, [int(supplier.id), supplier.name])

    def find(self, supplier_id):
        c = self._conn.cursor()
        c.execute("""
            SELECT * FROM suppliers WHERE id = ?
        """, [supplier_id])
        return Supplier(*c.fetchone())


class _Orders:
    def __init__(self, conn):
        self._conn = conn

    def insert(self,order):
        self._conn.execute("""INSERT INTO orders (id,location,hat) VALUES (?,?,?)""",[order.id,order.location,order.hat])   

    def find(self, order_id):
        c = self._conn.cursor()
        c.execute("""
            SELECT * FROM orders WHERE id = ?
        """, [order_id])
        return Order(*c.fetchone())
