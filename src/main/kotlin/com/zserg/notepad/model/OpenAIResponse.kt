package com.zserg.notepad.model

//{
//    "id": "chatcmpl-7NPeisQwFP1iDu5h5tqsORqqKab1i",
//    "object": "chat.completion",
//    "created": 1685814520,
//    "model": "gpt-3.5-turbo-0301",
//    "usage": {
//    "prompt_tokens": 31,
//    "completion_tokens": 220,
//    "total_tokens": 251
//},
//    "choices": [
//    {
//        "message": {
//        "role": "assistant",
//        "content": "Sharding is a horizontal scaling technique used in MongoDB to divide data into smaller chunks or shards and distribute them across multiple machines or servers. Each shard contains a subset of the data, and the shards together contain all the data in the database. \n\nSharding helps in achieving two important goals - scalability and high availability. Here's how:\n\n1. Scalability: As the data in a MongoDB database grows, a single server may not be able to handle the load. Sharding allows the data to be divided across multiple servers, each of which can handle a portion of the load. This helps to increase the capacity of the system and makes it easier to add additional capacity as needed. \n\n2. High availability: In a sharded MongoDB cluster, each shard is replicated across multiple servers to ensure high availability and data redundancy. If one shard fails, the system can continue to function, as the remaining shards can take on the load. \n\nIn summary, sharding in MongoDB allows for the creation of a highly scalable and available distributed system by partitioning data across multiple servers and ensuring redundancy and fault tolerance."
//    },
//        "finish_reason": "stop",
//        "index": 0
//    }
//    ]
//}
class OpenAIResponse {
    val id: String? = null
    val `object`: String? = null
    val created: Long? = null
    val model: String? = null
    val usage: Usage? = null
    val choices: List<Choice>? = null
    class Usage {
        val prompt_tokens: Int? = null
        val completion_tokens: Int? = null
        val total_tokens: Int? = null
    }

    class Choice {
        val message: Message? = null
        val finish_reason: String? = null
        val index: Int? = null
    }
    class Message {
        val role: String? = null
        val content: String? = null
    }
}
