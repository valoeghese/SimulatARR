# Suomenkieliset Populaatiosimulaattorin kartoitustiedostot

Nämä ovat avoimet, CC0-lisensoidut kartoitustiedostot Valoeghesen Populaatiosimulaattorille (engl. *Population Simulator*).

## Nimeämistavat

- Yhdyssana on yhdyssana, eli se kirjoitetaan yhteen.
  - Poikkeus: Yhdysmerkkiä käyttävät sanat kirjoitetaan symboleissa erikseen.
- Luokkien nimissä käytetään `UpperCamelCase`:a, muissa symboleissa `lowerCamelCase`:a.
  Staattisten ja lopullisten kenttien nimissä käytetään `UPPER_SNAKE_CASE`:a.
- Haku- ja asetusmetodien nimissä käytetään `hae`- ja `aseta`-etuliitteitä.
  Totuusarvojen hakumetodeissa käytetään `onko`-etuliitettä.
- Käytä tunnettuja Valoeghesen nimiä suomeksi käännettynä, mutta älä käännä pääluokan `Simulator` nimeä.
  - `Simulator.WORLD_DIAMETER` -> `Simulator.MAAILMAN_HALKAISIJA`
- `OpenSimplexNoise`-luokan nimet kuuluu pitää samoina kuin englanninkielisessä `master`-haarassa,
  sillä ne on otettu suoraan OpenSimplex-kohinan lähdekoodista.

## Huomioita

- Fabric-projektin Enigma ei tue ääkkösten käyttöä symboleissa,
  joten kartoitukseen täytyy käyttää omaa Enigma-versiotani (`finnish`-haara).
