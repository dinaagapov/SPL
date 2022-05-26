import atexit
import sqlite3
import sys
from DAO import _Hats, _Suppliers, _Orders


class _Repository:
    def __init__(self):
        self._conn = sqlite3.connect(sys.argv[4])
        self.hats = _Hats(self._conn)
        self.supplier = _Suppliers(self._conn)
        self.orders = _Orders(self._conn)

    def init(self):
        self.__init__()

    def _close(self):
        self._conn.commit()
        self._conn.close()

    def _commit(self):
        self._conn.commit()

    # def create_tables(self):
    #     self._conn.executescript("""
    #     CREATE TABLE suppliers (
    #         id    INTEGER  PRIMARY KEY,
    #         name  STRING     NOT NULL
    #     );
    #     CREATE TABLE hats (
    #         id       INTEGER  PRIMARY KEY,
    #         topping  STRING     NOT NULL,
    #         supplier INTEGER  REFERENCES Supplier(id),
    #         quantity INTEGER  NOT NULL
    #     );
    #     CREATE TABLE orders (
    #         id       INTEGER  PRIMARY KEY,
    #         location STRING     NOT NULL,
    #         hat INTEGER  REFERENCES hats(id)
    #     );
    #     """)

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE hats (
            id      INT     PRIMARY KEY,
            topping TEXT    NOT NULL,
            supplier    INT,
            quantity    INT NOT NULL,
            FOREIGN KEY(supplier)  REFERENCES supplier(id)
        );

        CREATE TABLE suppliers (
            id      INT     PRIMARY KEY,
            name TEXT    NOT NULL
        );

        CREATE TABLE orders (
            id      INT     PRIMARY KEY,
            location TEXT    NOT NULL,
            hat    INT,
            FOREIGN KEY(hat)  REFERENCES hats(id)
        );
    """)


repo = _Repository()
atexit.register(repo._close)
