# Net_game_client

Client of simple shooter game based on communication with sockets.

## Tools
Java 11, Swing, Maven

## Requirements
The client program requires a running [server](https://github.com/MonikaBer/Net_game_server), but the server isn't finished yet.

## Usage
After cloning the repository and entering it, run the following commands:
```bash
mvn package
java -jar target/client-1.0.0.jar
```

## Description

Klient ma 3 główne wątki do obsługi połączenia z serwerem. Pierwszy wątek (TcpHandler) jest uruchamiany w funkcji main. Z jego wnętrza zostaną uruchomione 2 pozostałe wątki: healthCheck (do wysyłania pakietów aktywności co 1 sek do serwera) oraz UdpWorker (do odbierania po UDP pakietów o stanie gry od serwera). Wysyłaniem pakietów UDP z ruchami i kątami strzałów gracza (klienta) zajmuje się controller (Controller.java), wyłapujący eventy klawiszowe z okna gry. Na razie, w wersji uproszczonej, gdy rozgrywka trwa, wystarczy wcisnąć dowolny klawisz klawiatury (będąc w oknie gry), a to spowoduje wysłanie pakietu UDP do serwera. Każdy pakiet UDP wysłany do serwera powoduje zwiększenie stanu gry (odsyłanego po UDP przez serwer) o wartość 5 (tymczasowe rozwiązanie w celach testowych). Stan gry jest na bieżąco wypisywany na konsolę.

Schemat działania TcpHandler'a:
- próba połączenia z serwerem po TCP
- czekanie na TCP na pakiet z przydzielonym id gracza od serwera
- uruchomienie wątku healthCheck 
- wysyłanie ostatnio otrzymanego od serwera pakietu (tego z id) po udp do serwera w pętli i jednoczesne oczekiwanie na pakiet "start" po TCP
- gdy pakiet "start" nadejdzie, przerwanie nadawania po udp 
- uruchomienie wątku UdpWorker i jednoczesne oczekiwanie na TCP na pakiet "stop" kończący rozgrywkę
- jeśli pakiet "stop" nadejdzie to wątek UdpWorker jest kończony i klient przechodzi ponownie w tryb oczekiwania na TCP na pakiet "start" rozpoczynający nową rozgrywkę

Do komunikacji po UDP zostały wykorzystane testowe pakiety. Klasy ze strukturą pakietów, które będą używane w docelowej wersji klienta znajdują się w "model/network/packets".
Na razie komunikacja odbywa się bez szyfrowania. Do tego celu została już przygotowana klasa w "model/cryptography".

