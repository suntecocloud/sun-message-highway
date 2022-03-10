#ifndef COMMON_H
#define COMMON_H

#if RD_KAFKA_VERSION < 0x000b0600
#error "Requires librdkafka v0.11.6 or later: see https://github.com/edenhill/librdkafka/blob/master/README.md#installing-prebuilt-packages"
#endif

extern int run;
rd_kafka_conf_t *read_config (const char *config_file);
int create_topic (rd_kafka_t *rk, const char *topic,
                  int num_partitions);

void error_cb (rd_kafka_t *rk, int err, const char *reason, void *opaque);

#endif /* COMMON_H */
