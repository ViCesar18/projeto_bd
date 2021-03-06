import psycopg2
import random
from datetime import datetime
from constants import NUMERO_USUARIOS

def gerar_posts(connection, cursor):
    i = 1

    query = """ INSERT INTO rede_musical.post (TEXTO_POST, DT_PUBLICACAO, USUARIO_ID) VALUES (%s,%s,%s)"""
    query_feed = """ INSERT INTO rede_musical.feed_possui_posts (FEED_ID, POST_ID, ID_USUARIO_COMPARTILHOU) VALUES (%s,%s, -1)"""
    query_seguidores = """ SELECT usuario_id FROM rede_musical.usuario_segue_usuario WHERE usuario_id_seguido = %s """

    while (i < 100):
        id_usuario = random.randint(1, NUMERO_USUARIOS)

        d1 = datetime.strptime('2021-01-01', '%Y-%m-%d')
        d2 = datetime.strptime('2018-01-01', '%Y-%m-%d')

        random_date = d1 + (d2 - d1) * random.random()

        insert = (
                'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 
                str(random_date.year) + '-' + str(random_date.month) + '-' + str(random_date.day), 
                id_usuario
                )

        insert_feed = (
            id_usuario,
            i
        )

        cursor.execute(query, insert)
        cursor.execute(query_feed, insert_feed)
        cursor.execute(query_seguidores, [id_usuario])
        
        row = cursor.fetchone()

        lista_seguidores = []
        while row is not None:
            lista_seguidores.append(row[0])
            row = cursor.fetchone()

        for j in lista_seguidores:
            insert_feed =(
                j,
                i
            )

            cursor.execute(query_feed, insert_feed)
        
        i+=1 


    connection.commit()
    count = cursor.rowcount 
    print(count, "Inserção de Posts finalizada")