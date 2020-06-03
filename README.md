# Net_game_client

Client of simple shooter game based on communication with sockets.

## Tools
Java 11, Swing, Maven

## Requirements


## Usage
After cloning the repository and entering it, run the following commands:
```bash
mvn package
java -jar target/client-1.0.0.jar
```

## Description

Klient ma 3 główne wątki do obsługi połączenia z serwerem. Pierwszy wątek (TcpHandler) jest uruchamiany w funkcji main. Z jego wnętrza zostaną uruchomione 2 pozostałe wątki: healthCheck (do wysyłania pakietów aktywności co 1 sek do serwera) oraz UdpWorker (do odbierania po UDP pakietów o stanie gry od serwera). Wysyłaniem pakietów UDP z ruchami i kątami strzałów gracza (klienta) zajmuje się controller, wyłapujący eventy klawiszowe z okna gry.Stan gry jest na bieżąco wypisywany na standardowe wyjście.


#### Uwagi:
Na razie komunikacja odbywa się bez szyfrowania. Do tego celu została już przygotowana klasa w "model/cryptography".
