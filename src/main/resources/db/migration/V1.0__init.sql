--
-- -- 创建向量嵌入表
-- CREATE TABLE IF NOT EXISTS ai.vector_store (
--     id VARCHAR(36) PRIMARY KEY,
--     content text ,
--     metadata json,
--     embedding vector(1536)
-- );
--
-- -- 创建向量索引
-- CREATE INDEX IF NOT EXISTS vector_embeddings_embedding_idx ON ai.vector_store USING HNSW (embedding vector_cosine_ops);