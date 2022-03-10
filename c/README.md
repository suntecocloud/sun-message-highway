
# Install librdkafka version 0.11.6
```
git clone https://github.com/edenhill/librdkafka.git
cd  librdkafka/
git checkout v0.11.6
./configure 
make
sudo make install
sudo ldconfig
```
# Run
```
 ./producer topic1 kafka.config 
 ./consumer topic1 kafka.config 
```