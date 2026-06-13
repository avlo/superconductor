class GenericEventRecord {
  constructor({
                id = null,
                publicKey = null,
                createdAt = null,
                kind = null,
                tags = [],
                content = "",
                sig = ""
              } = {}) {
    this._id = id;
    this._publicKey = publicKey;
    this._createdAt = createdAt;
    this._kind = kind;
    this._tags = Array.isArray(tags) ? tags : [];
    this._content = content;
    this._sig = sig;
  }

  static fromObject(node = {}) {
    return new GenericEventRecord({
      id: node.id ?? null,
      publicKey: node.pubkey ?? null,
      createdAt: node.created_at != null ? Number(node.created_at) : null,
      kind: node.kind ?? null,
      tags: Array.isArray(node.tags) ? node.tags : [],
      content: node.content ?? "",
      sig: node.sig ?? ""
    });
  }

  static unwrapEventJson(wrappedJson) {
    const parsed = JSON.parse(wrappedJson);

    if (!Array.isArray(parsed) || parsed.length < 2) {
      throw new Error('Invalid EVENT wrapper: expected ["EVENT", {...}]');
    }

    if (parsed[0] !== "EVENT") {
      throw new Error('Invalid EVENT wrapper: first element must be "EVENT"');
    }

    return parsed[1];
  }

  static fromJson(json) {
    return GenericEventRecord.fromObject(GenericEventRecord.unwrapEventJson(json));
  }

  toObject() {
    return {
      id: this._id,
      pubkey: this._publicKey,
      created_at: this._createdAt,
      kind: this._kind,
      tags: this._tags,
      content: this._content,
      sig: this._sig
    };
  }

  appendEventToObject() {
    return ["EVENT", this.toObject()];
  }

  toJson() {
    return JSON.stringify(this.appendEventToObject());
  }

  toPrettyJson() {
    return JSON.stringify(this.appendEventToObject(), null, 2)
  }

  getId() {
    return this._id;
  }

  setId(id) {
    this._id = id;
  }

  getPublicKey() {
    return this._publicKey;
  }

  setPublicKey(publicKey) {
    this._publicKey = publicKey;
  }

  getCreatedAt() {
    return this._createdAt;
  }

  setCreatedAt(createdAt) {
    this._createdAt = createdAt;
  }

  getKind() {
    return this._kind;
  }

  setKind(kind) {
    this._kind = kind;
  }

  getTags() {
    return this._tags;
  }

  setTags(tags) {
    this._tags = Array.isArray(tags) ? tags : [];
  }

  getContent() {
    return this._content;
  }

  setContent(content) {
    this._content = content;
  }

  getSig() {
    return this._sig;
  }

  setSig(sig) {
    this._sig = sig;
  }  
}

window.GenericEventRecord = GenericEventRecord;
