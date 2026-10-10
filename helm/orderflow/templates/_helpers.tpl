{{- define "orderflow.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "orderflow.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name (include "orderflow.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "orderflow.labels" -}}
app.kubernetes.io/name: {{ include "orderflow.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/environment: {{ .Values.environment | quote }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end -}}

{{- define "orderflow.environmentVariables" -}}
- name: MONGODB_URI
  valueFrom:
    secretKeyRef:
      name: {{ .Values.secrets.existingSecret }}
      key: {{ .Values.secrets.mongodbUriKey }}
- name: REDIS_HOST
  value: {{ .Values.config.redisHost | quote }}
- name: REDIS_PORT
  value: {{ .Values.config.redisPort | quote }}
- name: KAFKA_BOOTSTRAP_SERVERS
  value: {{ .Values.config.kafkaBootstrapServers | quote }}
- name: SCHEMA_REGISTRY_URL
  value: {{ .Values.config.schemaRegistryUrl | quote }}
{{- end -}}
