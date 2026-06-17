from locust import HttpUser, task, between

class SalePartidoUser(HttpUser):

    wait_time = between(1, 3)

    @task
    def buscar_locales(self):
        self.client.get("locales")
        # falta agregar alguna prueba mas pero para medir rendiminto y dispo esta sirve