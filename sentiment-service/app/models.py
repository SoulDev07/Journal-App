from pydantic import BaseModel


class SentimentRequest(BaseModel):
    """Mirrors the JournalEntryEvent from Java."""

    entry_id: str
    user_id: str
    content: str


class SentimentResponse(BaseModel):
    entry_id: str
    sentiment: str  # JOYFUL, SAD, ANGRY, ANXIOUS, DISGUSTED, SURPRISED, NEUTRAL
    confidence: float  # 0.0 to 1.0
