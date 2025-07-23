-- Crear tabla de auditoría de transacciones
CREATE TABLE transaction_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    transaction_number VARCHAR(255) NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(19,2),
    total_amount DECIMAL(19,2),
    user_id BIGINT NOT NULL,
    user_username VARCHAR(255) NOT NULL,
    account_id BIGINT,
    account_number VARCHAR(255),
    source_account_id BIGINT,
    source_account_number VARCHAR(255),
    destination_account_id BIGINT,
    destination_account_number VARCHAR(255),
    audit_event_type VARCHAR(50) NOT NULL,
    event_description VARCHAR(500),
    additional_data VARCHAR(1000),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para mejorar el rendimiento de las consultas
CREATE INDEX idx_transaction_audit_transaction_id ON transaction_audit_logs(transaction_id);
CREATE INDEX idx_transaction_audit_transaction_number ON transaction_audit_logs(transaction_number);
CREATE INDEX idx_transaction_audit_user_id ON transaction_audit_logs(user_id);
CREATE INDEX idx_transaction_audit_event_type ON transaction_audit_logs(audit_event_type);
CREATE INDEX idx_transaction_audit_created_at ON transaction_audit_logs(created_at);
CREATE INDEX idx_transaction_audit_account_id ON transaction_audit_logs(account_id);
CREATE INDEX idx_transaction_audit_account_number ON transaction_audit_logs(account_number);
CREATE INDEX idx_transaction_audit_source_account_id ON transaction_audit_logs(source_account_id);
CREATE INDEX idx_transaction_audit_destination_account_id ON transaction_audit_logs(destination_account_id);
CREATE INDEX idx_transaction_audit_ip_address ON transaction_audit_logs(ip_address);
CREATE INDEX idx_transaction_audit_session_id ON transaction_audit_logs(session_id);

-- Crear índices compuestos para consultas frecuentes
CREATE INDEX idx_transaction_audit_user_event ON transaction_audit_logs(user_id, audit_event_type);
CREATE INDEX idx_transaction_audit_date_event ON transaction_audit_logs(created_at, audit_event_type);
CREATE INDEX idx_transaction_audit_transaction_event ON transaction_audit_logs(transaction_id, audit_event_type);

-- Agregar comentarios a la tabla y columnas
COMMENT ON TABLE transaction_audit_logs IS 'Registro de auditoría de todas las transacciones bancarias';
COMMENT ON COLUMN transaction_audit_logs.id IS 'Identificador único del registro de auditoría';
COMMENT ON COLUMN transaction_audit_logs.transaction_id IS 'ID de la transacción auditada';
COMMENT ON COLUMN transaction_audit_logs.transaction_number IS 'Número único de la transacción';
COMMENT ON COLUMN transaction_audit_logs.previous_status IS 'Estado anterior de la transacción';
COMMENT ON COLUMN transaction_audit_logs.new_status IS 'Nuevo estado de la transacción';
COMMENT ON COLUMN transaction_audit_logs.transaction_type IS 'Tipo de transacción (DEPOSIT, WITHDRAWAL, etc.)';
COMMENT ON COLUMN transaction_audit_logs.amount IS 'Monto principal de la transacción';
COMMENT ON COLUMN transaction_audit_logs.total_amount IS 'Monto total incluyendo comisiones e impuestos';
COMMENT ON COLUMN transaction_audit_logs.user_id IS 'ID del usuario que realizó la transacción';
COMMENT ON COLUMN transaction_audit_logs.user_username IS 'Nombre de usuario';
COMMENT ON COLUMN transaction_audit_logs.account_id IS 'ID de la cuenta principal';
COMMENT ON COLUMN transaction_audit_logs.account_number IS 'Número de cuenta principal';
COMMENT ON COLUMN transaction_audit_logs.source_account_id IS 'ID de la cuenta origen (para transferencias)';
COMMENT ON COLUMN transaction_audit_logs.source_account_number IS 'Número de cuenta origen';
COMMENT ON COLUMN transaction_audit_logs.destination_account_id IS 'ID de la cuenta destino (para transferencias)';
COMMENT ON COLUMN transaction_audit_logs.destination_account_number IS 'Número de cuenta destino';
COMMENT ON COLUMN transaction_audit_logs.audit_event_type IS 'Tipo de evento de auditoría';
COMMENT ON COLUMN transaction_audit_logs.event_description IS 'Descripción detallada del evento';
COMMENT ON COLUMN transaction_audit_logs.additional_data IS 'Datos adicionales en formato JSON';
COMMENT ON COLUMN transaction_audit_logs.ip_address IS 'Dirección IP del cliente';
COMMENT ON COLUMN transaction_audit_logs.user_agent IS 'User agent del navegador';
COMMENT ON COLUMN transaction_audit_logs.session_id IS 'ID de la sesión del usuario';
COMMENT ON COLUMN transaction_audit_logs.created_at IS 'Fecha y hora de creación del registro de auditoría'; 