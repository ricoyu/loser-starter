# 一 Maven依赖

```xml
<dependency>
	<groupId>com.loserico</groupId>
	<artifactId>loser-spring-boot-es-starter</artifactId>
	<version>2.6.2</version>
</dependency>
```

# 二 elastic.properties

```properties
#cluster.name=nta
#elastic.hosts=10.10.26.240:9300
#elastic.rest.hosts=10.10.26.240:9200
        
cluster.name=nta
elastic.hosts=172.23.12.65:9300
elastic.rest.hosts=172.23.12.65:9200
```



# 二 模板配置

1. application.yaml添加

   ```yaml
   loser:
     es:
       init: true
       templates:
         - netlog_template.json
   ```

   netlog_template.json可以用如下几种路径格式来指定:

   * 加上 classpath: 前缀指定从classpath下读取
   * 加上 / 指定从文件系统中读取
   * 如果没有指定前缀的话, 按照如下优先级从高到低顺序读取
     * 当前工作目录/config/ 下读取
     * 当前工作目录
     * classpath

   netlog_template.json示例: [metadata.json](..\..\..\Work\GuanAn\nta-parent\nta-index-service\src\main\resources\metadata.json) 

   ```json
   {
     "index_patterns": [
       "netlog_*"
     ],
     "settings": {
       "index": {
         "refresh_interval": "60s",
         "number_of_shards": 1,
         "number_of_replicas": 0,
         "max_result_window": 100000000
       },
       "routing": {
         "allocation": {
           "total_shards_per_node": 1
         }
       },
       "translog": {
         "sync_interval": "5m",
         "durability": "async",
         "flush_threshold_size": "2G"
       },
       "analysis": {
         "tokenizer": {
           "domain": {
             "type": "char_group",
             "tokenize_on_chars": [
               ".",
               "/",
               "whitespace"
             ]
           },
           "file": {
             "type": "char_group",
             "tokenize_on_chars": [
               ".",
               "/",
               "whitespace"
             ]
           }
         },
         "analyzer": {
           "domain": {
             "tokenizer": "domain",
             "filter": [
               "lowercase"
             ]
           },
           "file": {
             "tokenizer": "file",
             "filter": [
               "lowercase"
             ]
           }
         }
       }
     },
     "mappings": {
       "dynamic": false,
       "properties": {
         "id": {
           "type": "keyword",
           "norms": false
         },
         "geo": {
           "type": "boolean"
         },
         "dev_id": {
           "type": "keyword",
           "norms": false
         },
         "create_time": {
           "type": "date",
           "format": "epoch_millis"
         },
         "seq": {
           "type": "long"
         },
         "proto_type": {
           "type": "keyword",
           "norms": false
         },
         "event_type": {
           "type": "keyword",
           "norms": false
         },
         "proto": {
           "type": "keyword",
           "norms": false
         },
         "src_ip": {
           "type": "ip"
         },
         "src_port": {
           "type": "long"
         },
         "flow_id": {
           "type": "keyword",
           "norms": false
         },
         "in_iface": {
           "type": "keyword",
           "norms": false
         },
         "branch_id": {
           "type": "keyword",
           "norms": false
         },
         "src_owner ": {
           "type": "keyword",
           "norms": false
         },
         "src_province": {
           "type": "keyword",
           "norms": false
         },
         "src_location_lat": {
           "type": "keyword",
           "norms": false
         },
         "src_location_lon": {
           "type": "keyword",
           "norms": false
         },
         "data": {
           "properties": {
             "domain": {
               "type": "text",
               "analyzer": "domain",
               "norms": false,
               "fields": {
                 "keyword": {
                   "type": "keyword",
                   "ignore_above": 256
                 }
               }
             },
             "url": {
               "type": "keyword",
               "norms": false
             },
             "http_origin": {
               "type": "keyword",
               "norms": false
             },
             "http_referer": {
               "type": "keyword",
               "norms": false
             },
             "user_agent": {
               "type": "keyword",
               "norms": false
             },
             "response_content_type": {
               "type": "keyword",
               "norms": false
             },
             "request_content_type": {
               "type": "keyword",
               "norms": false
             },
             "http_cookie": {
               "type": "keyword",
               "norms": false
             },
             "http_content_length": {
               "type": "long"
             },
             "status": {
               "type": "long"
             },
             "http_protocol": {
               "type": "keyword",
               "norms": false
             },
             "http_method": {
               "type": "keyword",
               "norms": false
             },
             "response_authorization": {
               "type": "keyword",
               "norms": false
             },
             "http_allow": {
               "type": "keyword",
               "norms": false
             },
             "http_accept": {
               "type": "keyword",
               "norms": false
             },
             "http_accept_charset": {
               "type": "keyword",
               "norms": false
             },
             "http_accept_encoding": {
               "type": "keyword",
               "norms": false
             },
             "http_accept_language": {
               "type": "keyword",
               "norms": false
             },
             "response_datetime": {
               "type": "keyword",
               "norms": false
             },
             "http_connection": {
               "type": "keyword",
               "norms": false
             },
             "http_xff": {
               "type": "keyword",
               "norms": false
             },
             "http_true_client_ip": {
               "type": "keyword",
               "norms": false
             },
             "http_org_src_ip": {
               "type": "keyword",
               "norms": false
             },
             "post_data": {
               "type": "keyword",
               "norms": false
             },
             "response_body": {
               "type": "keyword",
               "norms": false
             },
             "tls_subject": {
               "type": "keyword",
               "norms": false
             },
             "tls_issuerdn": {
               "type": "keyword",
               "norms": false
             },
             "tls_serial": {
               "type": "keyword",
               "norms": false
             },
             "tls_fingerprint": {
               "type": "keyword",
               "norms": false
             },
             "tls_sni": {
               "type": "keyword",
               "norms": false
             },
             "tls_version": {
               "type": "keyword",
               "norms": false
             },
             "tls_notbefore": {
               "type": "keyword",
               "norms": false
             },
             "tls_notafter": {
               "type": "keyword",
               "norms": false
             },
             "tls_ja3_hash": {
               "type": "keyword",
               "norms": false
             },
             "tls_ja3_string": {
               "type": "keyword",
               "norms": false
             },
             "tls_ja3s_hash": {
               "type": "keyword",
               "norms": false
             },
             "tls_ja3s_string": {
               "type": "keyword",
               "norms": false
             },
             "ssh_client_proto_version": {
               "type": "keyword",
               "norms": false
             },
             "ssh_client_software_version": {
               "type": "keyword",
               "norms": false
             },
             "ssh_server_proto_version": {
               "type": "keyword",
               "norms": false
             },
             "ssh_server_software_version": {
               "type": "keyword",
               "norms": false
             },
             "dns_version": {
               "type": "keyword",
               "norms": false
             },
             "dns_type": {
               "type": "keyword",
               "norms": false
             },
             "dns_id": {
               "type": "keyword",
               "norms": false
             },
             "dns_flags": {
               "type": "keyword",
               "norms": false
             },
             "dns_qr": {
               "type": "keyword",
               "norms": false
             },
             "dns_aa": {
               "type": "keyword",
               "norms": false
             },
             "dns_tc": {
               "type": "keyword",
               "norms": false
             },
             "dns_rd": {
               "type": "keyword",
               "norms": false
             },
             "dns_ra": {
               "type": "keyword",
               "norms": false
             },
             "dns_rrtype": {
               "type": "keyword",
               "norms": false
             },
             "dns_rcode": {
               "type": "keyword",
               "norms": false
             },
             "dns_tx_id": {
               "type": "keyword",
               "norms": false
             },
             "dns_grouped_cname": {
               "type": "text",
               "analyzer": "english",
               "norms": false,
               "fields": {
                 "keyword": {
                   "type": "keyword",
                   "ignore_above": 256
                 }
               }
             },
             "dns_grouped_a": {
               "type": "text",
               "analyzer": "english",
               "norms": false,
               "fields": {
                 "keyword": {
                   "type": "keyword",
                   "ignore_above": 256
                 }
               }
             },
             "dhcp_type": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_id": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_client_mac": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_assigned_ip": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_client_ip": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_relay_ip": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_requested_ip": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_hostname": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_subnet_mask": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_dhcp_type": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_routers": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_dns_servers": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_params": {
               "type": "keyword",
               "norms": false
             },
             "dhcp_option55": {
               "type": "keyword",
               "norms": false
             },
             "icmp_type": {
               "type": "keyword",
               "norms": false
             },
             "icmp_code": {
               "type": "long"
             },
             "icmp_id": {
               "type": "long"
             },
             "icmp_sequence": {
               "type": "long"
             },
             "icmp_checksum": {
               "type": "long"
             },
             "icmp_timestamp": {
               "enabled": "false"
             },
             "icmp_payload_len": {
               "type": "long"
             },
             "icmp_payload": {
               "type": "keyword",
               "norms": false
             },
             "ftp_command": {
               "type": "keyword",
               "norms": false
             },
             "ftp_command_data": {
               "type": "keyword",
               "norms": false
             },
             "ftp_reply": {
               "type": "keyword",
               "norms": false
             },
             "ftp_completion_code": {
               "type": "keyword",
               "norms": false
             },
             "ftp_dynamic_port": {
               "type": "keyword",
               "norms": false
             },
             "ftp_mode": {
               "type": "keyword",
               "norms": false
             },
             "ftp_reply_received": {
               "type": "keyword",
               "norms": false
             },
             "ftp_username": {
               "type": "keyword",
               "norms": false
             },
             "ftp_password": {
               "type": "keyword",
               "norms": false
             },
             "ftp_auth_success": {
               "type": "long"
             },
             "smb_id": {
               "type": "keyword",
               "norms": false
             },
             "smb_dialect": {
               "type": "keyword",
               "norms": false
             },
             "smb_command": {
               "type": "keyword",
               "norms": false
             },
             "smb_status": {
               "type": "keyword",
               "norms": false
             },
             "smb_status_code": {
               "type": "keyword",
               "norms": false
             },
             "smb_session_id": {
               "type": "keyword",
               "norms": false
             },
             "smb_tree_id": {
               "type": "keyword",
               "norms": false
             },
             "filename": {
               "type": "keyword",
               "norms": false
             },
             "smb_disposition": {
               "type": "keyword",
               "norms": false
             },
             "smb_access": {
               "type": "keyword",
               "norms": false
             },
             "smb_size": {
               "type": "keyword",
               "norms": false
             },
             "smb_fuid": {
               "type": "keyword",
               "norms": false
             },
             "smb_share": {
               "type": "keyword",
               "norms": false
             },
             "smb_share_type": {
               "type": "keyword",
               "norms": false
             },
             "smb_client_dialects": {
               "type": "keyword",
               "norms": false
             },
             "smb_client_guid": {
               "type": "keyword",
               "norms": false
             },
             "smb_server_guid": {
               "type": "keyword",
               "norms": false
             },
             "smb_request_native_os": {
               "type": "keyword",
               "norms": false
             },
             "smb_request_native_lm": {
               "type": "keyword",
               "norms": false
             },
             "smb_response_native_os": {
               "type": "keyword",
               "norms": false
             },
             "smb_response_native_lm": {
               "type": "keyword",
               "norms": false
             },
             "smb_created": {
               "type": "keyword",
               "norms": false
             },
             "smb_accessed": {
               "type": "keyword",
               "norms": false
             },
             "smb_modified": {
               "type": "keyword",
               "norms": false
             },
             "smb_changed": {
               "type": "keyword",
               "norms": false
             },
             "smb_ntlmssp_domain": {
               "type": "keyword",
               "norms": false
             },
             "smb_ntlmssp_user": {
               "type": "keyword",
               "norms": false
             },
             "smb_ntlmssp_host": {
               "type": "keyword",
               "norms": false
             },
             "rpc_xid": {
               "type": "keyword",
               "norms": false
             },
             "rpc_status": {
               "type": "keyword",
               "norms": false
             },
             "rpc_auth_type": {
               "type": "keyword",
               "norms": false
             },
             "rpc_creds_machine_name": {
               "type": "keyword",
               "norms": false
             },
             "rpc_creds_uid": {
               "type": "keyword",
               "norms": false
             },
             "rpc_creds_gid": {
               "type": "keyword",
               "norms": false
             },
             "nfs_version": {
               "type": "keyword",
               "norms": false
             },
             "nfs_procedure": {
               "type": "keyword",
               "norms": false
             },
             "nfs_hhash": {
               "type": "keyword",
               "norms": false
             },
             "nfs_id": {
               "type": "keyword",
               "norms": false
             },
             "nfs_file_tx": {
               "type": "keyword",
               "norms": false
             },
             "nfs_type": {
               "type": "keyword",
               "norms": false
             },
             "nfs_status": {
               "type": "keyword",
               "norms": false
             },
             "smtp_helo": {
               "type": "keyword",
               "norms": false
             },
             "smtp_mail_from": {
               "type": "keyword",
               "norms": false
             },
             "smtp_rcpt_to": {
               "type": "keyword",
               "norms": false
             },
             "email_username": {
               "type": "keyword",
               "norms": false
             },
             "email_password": {
               "type": "keyword",
               "norms": false
             },
             "email_status": {
               "type": "keyword",
               "norms": false
             },
             "email_from": {
               "type": "keyword",
               "norms": false
             },
             "email_to": {
               "type": "keyword",
               "norms": false
             },
             "email_cc": {
               "type": "keyword",
               "norms": false
             },
             "email_subject": {
               "type": "keyword",
               "norms": false
             },
             "email_attachment": {
               "type": "keyword",
               "norms": false
             },
             "postgresql_cmd": {
               "type": "keyword",
               "norms": false
             },
             "task_id": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_type": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_control_dir": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_control_pri": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_control_fcb": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_control_fcv": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_control_function_code": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_src": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_dst": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_control_fir": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_control_fin": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_control_con": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_control_uns": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_control_sequence": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_function_code": {
               "type": "keyword",
               "norms": false
             },
             "dnp3_application_complete": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_command": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_length": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_session": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_context": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_status": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_option": {
               "type": "keyword",
               "norms": false
             },
             "enip_header_message": {
               "type": "keyword",
               "norms": false
             },
             "enip_data_header_interface_handle": {
               "type": "keyword",
               "norms": false
             },
             "enip_data_header_item_count": {
               "type": "keyword",
               "norms": false
             },
             "enip_data_header_timeout": {
               "type": "keyword",
               "norms": false
             },
             "enip_data_item_type": {
               "type": "keyword",
               "norms": false
             },
             "enip_data_item_length": {
               "type": "keyword",
               "norms": false
             },
             "enip_data_item_sequence_count": {
               "type": "keyword",
               "norms": false
             },
             "enip_address_item_type": {
               "type": "keyword",
               "norms": false
             },
             "enip_address_item_length": {
               "type": "keyword",
               "norms": false
             },
             "enip_address_item_conn_id": {
               "type": "keyword",
               "norms": false
             },
             "enip_address_item_sequence_num": {
               "type": "keyword",
               "norms": false
             },
             "modbus_transaction_id": {
               "type": "keyword",
               "norms": false
             },
             "modbus_unit_id": {
               "type": "keyword",
               "norms": false
             },
             "modbus_function_raw": {
               "type": "keyword",
               "norms": false
             },
             "modbus_function_code": {
               "type": "keyword",
               "norms": false
             },
             "modbus_category": {
               "type": "keyword",
               "norms": false
             },
             "modbus_replied": {
               "type": "keyword",
               "norms": false
             },
             "snmp_version": {
               "type": "keyword",
               "norms": false
             },
             "snmp_pdu_type": {
               "type": "keyword",
               "norms": false
             },
             "snmp_vars": {
               "type": "keyword",
               "norms": false
             },
             "snmp_community": {
               "type": "keyword",
               "norms": false
             },
             "snmp_error": {
               "type": "keyword",
               "norms": false
             },
             "snmp_trap_type": {
               "type": "keyword",
               "norms": false
             },
             "snmp_trap_oid": {
               "type": "keyword",
               "norms": false
             },
             "trap_address": {
               "type": "keyword",
               "norms": false
             },
             "usm": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_message_type": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_qos": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_protocol_string": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_protocol_version": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_client_id": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_flags_username": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_flags_password": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_username": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_password": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_will_topic": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_connect_will_message": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_publish_topic": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_publish_message": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_publish_message_id": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_subscribe_message_id": {
               "type": "keyword",
               "norms": false
             },
             "mqtt_subscribe_topics": {
               "type": "keyword",
               "norms": false
             },
             "sip_method": {
               "type": "keyword",
               "norms": false
             },
             "sip_uri": {
               "type": "keyword",
               "norms": false
             },
             "sip_version": {
               "type": "keyword",
               "norms": false
             },
             "sip_request_line": {
               "type": "keyword",
               "norms": false
             },
             "sip_code": {
               "type": "keyword",
               "norms": false
             },
             "sip_reason": {
               "type": "keyword",
               "norms": false
             },
             "sip_response_line": {
               "type": "keyword",
               "norms": false
             },
             "ntp_ref_id": {
               "type": "keyword",
               "norms": false
             },
             "ntp_version": {
               "type": "keyword",
               "norms": false
             },
             "ntp_ts_ref": {
               "type": "keyword",
               "norms": false
             },
             "ntp_ts_orig": {
               "type": "keyword",
               "norms": false
             },
             "ntp_ts_recv": {
               "type": "keyword",
               "norms": false
             },
             "ntp_ts_xmit": {
               "type": "keyword",
               "norms": false
             },
             "goose_appid": {
               "type": "keyword",
               "norms": false
             },
             "goose_length": {
               "type": "keyword",
               "norms": false
             },
             "goose_gocbref": {
               "type": "keyword",
               "norms": false
             },
             "goose_timealive": {
               "type": "keyword",
               "norms": false
             },
             "goose_datset": {
               "type": "keyword",
               "norms": false
             },
             "goose_goid": {
               "type": "keyword",
               "norms": false
             },
             "goose_timestamp": {
               "type": "keyword",
               "norms": false
             },
             "goose_stnum": {
               "type": "keyword",
               "norms": false
             },
             "goose_sqnum": {
               "type": "keyword",
               "norms": false
             },
             "goose_simulation": {
               "type": "keyword",
               "norms": false
             },
             "goose_confrev": {
               "type": "keyword",
               "norms": false
             },
             "goose_ndscom": {
               "type": "keyword",
               "norms": false
             },
             "goose_num_datset_entries": {
               "type": "keyword",
               "norms": false
             },
             "iec104_code": {
               "type": "keyword",
               "norms": false
             },
             "iec104_length": {
               "type": "keyword",
               "norms": false
             },
             "iec104_command": {
               "type": "keyword",
               "norms": false
             },
             "iec104_tx_seq": {
               "type": "keyword",
               "norms": false
             },
             "iec104_rx_seq": {
               "type": "keyword",
               "norms": false
             },
             "iec104_itype": {
               "type": "keyword",
               "norms": false
             },
             "iec104_sq": {
               "type": "keyword",
               "norms": false
             },
             "iec104_test": {
               "type": "keyword",
               "norms": false
             },
             "iec104_pn": {
               "type": "keyword",
               "norms": false
             },
             "iec104_reason": {
               "type": "keyword",
               "norms": false
             },
             "iec104_src_address": {
               "type": "keyword",
               "norms": false
             },
             "iec104_pub_address": {
               "type": "keyword",
               "norms": false
             },
             "iec104_message_length": {
               "type": "keyword",
               "norms": false
             },
             "radius_code": {
               "type": "keyword",
               "norms": false
             },
             "radius_identifier": {
               "type": "keyword",
               "norms": false
             },
             "radius_length": {
               "type": "keyword",
               "norms": false
             },
             "radius_authenticator": {
               "type": "keyword",
               "norms": false
             },
             "radius_username": {
               "type": "keyword",
               "norms": false
             },
             "radius_user_password": {
               "type": "keyword",
               "norms": false
             },
             "radius_chap_password": {
               "type": "keyword",
               "norms": false
             },
             "radius_nas_ip_addr": {
               "type": "keyword",
               "norms": false
             },
             "radius_nas_port": {
               "type": "keyword",
               "norms": false
             },
             "radius_service_type": {
               "type": "keyword",
               "norms": false
             },
             "radius_framed_protocol": {
               "type": "keyword",
               "norms": false
             },
             "radius_frame_ip_addr": {
               "type": "keyword",
               "norms": false
             },
             "radius_frame_ip_netmask": {
               "type": "keyword",
               "norms": false
             },
             "radius_framed_routing": {
               "type": "keyword",
               "norms": false
             },
             "radius_filter_id": {
               "type": "keyword",
               "norms": false
             },
             "radius_framed_mtu": {
               "type": "keyword",
               "norms": false
             },
             "radius_framed_compression": {
               "type": "keyword",
               "norms": false
             },
             "radius_called_station_id": {
               "type": "keyword",
               "norms": false
             },
             "radius_calling_station_id": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_version_major": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_version_minor": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_exchange_type": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_message_id": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_init_spi": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_resp_spi": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_role": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_alg_enc": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_alg_auth": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_alg_prf": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_alg_dh": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_alg_esn": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_errors": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_payload": {
               "type": "keyword",
               "norms": false
             },
             "ikev2_notify": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_opcode": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_key": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_session_id": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_remote_session_id": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_hmac": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_packet_id": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_net_time": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_msg_packet_id": {
               "type": "keyword",
               "norms": false
             },
             "openvpn_data_len": {
               "type": "keyword",
               "norms": false
             },
             "http_version": {
               "type": "keyword",
               "norms": false
             },
             "http_http2_stream_id": {
               "type": "keyword",
               "norms": false
             },
             "http_http2_request_settings": {
               "enabled": false
             },
             "http_http2_response_settings": {
               "enabled": false
             },
             "http_url": {
               "type": "keyword",
               "norms": false
             },
             "http_length": {
               "type": "keyword",
               "norms": false
             },
             "http_hostname": {
               "type": "keyword",
               "norms": false
             },
             "http_http_user_agent": {
               "type": "keyword",
               "norms": false
             },
             "http_http_method": {
               "type": "keyword",
               "norms": false
             },
             "http_status": {
               "type": "keyword",
               "norms": false
             },
             "tftp_packet": {
               "type": "keyword",
               "norms": false
             },
             "tftp_file": {
               "type": "keyword",
               "norms": false
             },
             "tftp_mode": {
               "type": "keyword",
               "norms": false
             },
             "pptp_type": {
               "type": "keyword",
               "norms": false
             },
             "pptp_hostname": {
               "type": "keyword",
               "norms": false
             },
             "pptp_vendorname": {
               "type": "keyword",
               "norms": false
             },
             "pptp_phone_number": {
               "type": "keyword",
               "norms": false
             },
             "pptp_result_code": {
               "type": "keyword",
               "norms": false
             },
             "pptp_physical_channel_id": {
               "type": "keyword",
               "norms": false
             },
             "pptp_maximum_channels": {
               "type": "keyword",
               "norms": false
             },
             "pptp_firmware_reversion": {
               "type": "keyword",
               "norms": false
             },
             "pptp_subaddress": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail_from": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail_to": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail_cc": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail_bcc": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail_subject": {
               "type": "keyword",
               "norms": false
             },
             "http_webmail_body": {
               "type": "keyword",
               "norms": false
             },
             "email_body": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_speed": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_client_hostname": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_client_vendorname": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_server_hostname": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_server_vendorname": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_ppp_max_unit": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_ppp_servername": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_ppp_clientname": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_ppp_ip_server": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_ppp_ip_client": {
               "type": "keyword",
               "norms": false
             },
             "l2tp_ppp_algotithm": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_request": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_interfaces": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_req_opnum": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_req_frag_cnt": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_req_stub_data_size": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_response": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_call_id": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_rpc_version": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_activityuuid": {
               "type": "keyword",
               "norms": false
             },
             "dcerpc_seqnum": {
               "type": "keyword",
               "norms": false
             },
             "krb_msg_type": {
               "type": "keyword",
               "norms": false
             },
             "krb_failed_request": {
               "type": "keyword",
               "norms": false
             },
             "krb_error_code": {
               "type": "keyword",
               "norms": false
             },
             "krb_cname": {
               "type": "keyword",
               "norms": false
             },
             "krb_realm": {
               "type": "keyword",
               "norms": false
             },
             "krb_sname": {
               "type": "keyword",
               "norms": false
             },
             "krb_encryption": {
               "type": "keyword",
               "norms": false
             },
             "krb_weak_encryption": {
               "type": "keyword",
               "norms": false
             },
             "rfb_server_protocol_version_major": {
               "type": "keyword",
               "norms": false
             },
             "rfb_server_protocol_version_minor": {
               "type": "keyword",
               "norms": false
             },
             "rfb_client_protocol_version_major": {
               "type": "keyword",
               "norms": false
             },
             "rfb_client_protocol_version_minor": {
               "type": "keyword",
               "norms": false
             },
             "rfb_authentication_security_type": {
               "type": "keyword",
               "norms": false
             },
             "rfb_authentication_vnc_challenge": {
               "type": "keyword",
               "norms": false
             },
             "rfb_authentication_vnc_response": {
               "type": "keyword",
               "norms": false
             },
             "rfb_authentication_security_result": {
               "type": "keyword",
               "norms": false
             },
             "rfb_screen_shared": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffe r_width": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_height": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_name": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_bits_per_pixel": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_depth": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_big_endian": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_true_color": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_red_max": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_green_max": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_blue_max": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_red_shift": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_green_shift": {
               "type": "keyword",
               "norms": false
             },
             "rfb_framebuffer_pixel_format_blue_shift": {
               "type": "keyword",
               "norms": false
             },
             "teamviewer_id": {
               "type": "keyword",
               "norms": false
             },
             "pcanywhere_flag": {
               "type": "keyword",
               "norms": false
             },
             "qq_id": {
               "type": "keyword",
               "norms": false
             },
             "qq_outside_addr": {
               "type": "keyword",
               "norms": false
             },
             "qq_local_port": {
               "type": "keyword",
               "norms": false
             },
             "qq_client_type": {
               "type": "keyword",
               "norms": false
             },
             "qq_version": {
               "type": "keyword",
               "norms": false
             },
             "http_response_headers": {
               "enabled": false
             },
             "http_request_headers": {
               "enabled": false
             },
             "http_event_coord": {
               "type": "keyword",
               "norms": false
             },
             "http_request_header_raw": {
               "type": "keyword",
               "norms": false
             },
             "http_response_header_raw": {
               "type": "keyword",
               "norms": false
             },
             "asset": {
               "properties": {
                 "ip": {
                   "type": "ip"
                 },
                 "group": {
                   "type": "text",
                   "norms": false,
                   "analyzer": "ik_smart",
                   "fields": {
                     "keyword": {
                       "type": "keyword",
                       "ignore_above": 256
                     }
                   }
                 },
                 "name": {
                   "type": "text",
                   "norms": false,
                   "analyzer": "ik_smart",
                   "fields": {
                     "keyword": {
                       "type": "keyword",
                       "ignore_above": 256
                     }
                   }
                 },
                 "owner": {
                   "type": "keyword",
                   "norms": false
                 },
                 "business_system": {
                   "type": "keyword",
                   "norms": false
                 },
                 "dapartment": {
                   "type": "keyword",
                   "norms": false
                 },
                 "logical_address": {
                   "type": "keyword",
                   "norms": false
                 },
                 "physical_address": {
                   "type": "keyword",
                   "norms": false
                 },
                 "operating_system": {
                   "type": "keyword",
                   "norms": false
                 },
                 "manufacture": {
                   "type": "keyword",
                   "norms": false
                 },
                 "device_model": {
                   "type": "keyword",
                   "norms": false
                 },
                 "register_date": {
                   "type": "keyword",
                   "norms": false
                 },
                 "label": {
                   "type": "keyword",
                   "norms": false
                 },
                 "remark": {
                   "type": "keyword",
                   "norms": false
                 }
               }
             },
             "uplink_pkts": {
               "type": "keyword",
               "norms": false
             },
             "downlink_pkts": {
               "type": "keyword",
               "norms": false
             },
             "uplink_length": {
               "type": "keyword",
               "norms": false
             },
             "downlink_length": {
               "type": "keyword",
               "norms": false
             },
             "udp_stime": {
               "type": "keyword",
               "norms": false
             },
             "tcp_stime": {
               "type": "keyword",
               "norms": false
             },
             "icmp_stime": {
               "type": "keyword",
               "norms": false
             },
             "udp_dtime": {
               "type": "keyword",
               "norms": false
             },
             "tcp_dtime": {
               "type": "keyword",
               "norms": false
             },
             "icmp_dtime": {
               "type": "keyword",
               "norms": false
             },
             "flow_age": {
               "type": "keyword",
               "norms": false
             },
             "flow_speed": {
               "type": "keyword",
               "norms": false
             },
             "flow_state": {
               "type": "keyword",
               "norms": false
             },
             "udp_status": {
               "type": "keyword",
               "norms": false
             },
             "tcp_status": {
               "type": "keyword",
               "norms": false
             },
             "icmp_status": {
               "type": "keyword",
               "norms": false
             },
             "flow_alerted": {
               "type": "keyword",
               "norms": false
             },
             "response_icmp_type": {
               "type": "keyword",
               "norms": false
             },
             "response_icmp_code": {
               "type": "keyword",
               "norms": false
             },
             "up_payload": {
               "type": "keyword",
               "norms": false
             },
             "down_payload": {
               "type": "keyword",
               "norms": false
             },
             "flow_ndpi_name": {
               "type": "keyword",
               "norms": false
             },
             "src_mac": {
               "type": "keyword",
               "norms": false
             },
             "dst_mac": {
               "type": "keyword",
               "norms": false
             }
           }
         }
       }
     }
   }
   ```

   

