-- Crear tabla de transacciones
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_number VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'PAYMENT', 'REFUND', 'FEE_CHARGE')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REVERSED')),
    amount DECIMAL(19,2) NOT NULL CHECK (amount > 0),
    fee DECIMAL(19,2) DEFAULT 0.00,
    tax DECIMAL(19,2) DEFAULT 0.00,
    total_amount DECIMAL(19,2) NOT NULL,
    description VARCHAR(500),
    notes VARCHAR(1000),
    
    -- Relaciones con cuentas
    source_account_id BIGINT REFERENCES accounts(id),
    destination_account_id BIGINT REFERENCES accounts(id),
    account_id BIGINT REFERENCES accounts(id),
    
    -- Relación con usuario
    user_id BIGINT NOT NULL REFERENCES users(id),
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    
    -- Índices para mejorar performance
    CONSTRAINT idx_transactions_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT idx_transactions_account_id FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT idx_transactions_source_account_id FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    CONSTRAINT idx_transactions_destination_account_id FOREIGN KEY (destination_account_id) REFERENCES accounts(id)
);

-- Crear índices para mejorar las consultas
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_source_account_id ON transactions(source_account_id);
CREATE INDEX idx_transactions_destination_account_id ON transactions(destination_account_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_transaction_number ON transactions(transaction_number);

-- Crear índices compuestos para consultas frecuentes
CREATE INDEX idx_transactions_user_status ON transactions(user_id, status);
CREATE INDEX idx_transactions_user_type ON transactions(user_id, type);
CREATE INDEX idx_transactions_user_created_at ON transactions(user_id, created_at);
CREATE INDEX idx_transactions_account_status ON transactions(account_id, status);
CREATE INDEX idx_transactions_account_type ON transactions(account_id, type);

-- Comentarios para documentar la tabla
COMMENT ON TABLE transactions IS 'Tabla que almacena todas las transacciones bancarias del sistema';
COMMENT ON COLUMN transactions.id IS 'Identificador único de la transacción';
COMMENT ON COLUMN transactions.transaction_number IS 'Número único de transacción generado automáticamente';
COMMENT ON COLUMN transactions.type IS 'Tipo de transacción: DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, REFUND, FEE_CHARGE';
COMMENT ON COLUMN transactions.status IS 'Estado actual de la transacción: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REVERSED';
COMMENT ON COLUMN transactions.amount IS 'Monto principal de la transacción';
COMMENT ON COLUMN transactions.fee IS 'Comisión aplicada a la transacción';
COMMENT ON COLUMN transactions.tax IS 'Impuesto aplicado a la transacción';
COMMENT ON COLUMN transactions.total_amount IS 'Monto total (amount + fee + tax)';
COMMENT ON COLUMN transactions.description IS 'Descripción de la transacción';
COMMENT ON COLUMN transactions.notes IS 'Notas adicionales sobre la transacción';
COMMENT ON COLUMN transactions.source_account_id IS 'Cuenta origen (para transferencias)';
COMMENT ON COLUMN transactions.destination_account_id IS 'Cuenta destino (para transferencias)';
COMMENT ON COLUMN transactions.account_id IS 'Cuenta principal (para depósitos/retiros)';
COMMENT ON COLUMN transactions.user_id IS 'Usuario que realiza la transacción';
COMMENT ON COLUMN transactions.created_at IS 'Fecha y hora de creación de la transacción';
COMMENT ON COLUMN transactions.updated_at IS 'Fecha y hora de última actualización';
COMMENT ON COLUMN transactions.processed_at IS 'Fecha y hora de procesamiento de la transacción'; 