import { onDocumentCreated } from "firebase-functions/v2/firestore";
import { initializeApp } from "firebase-admin/app";
import { getFirestore } from "firebase-admin/firestore";
import { getMessaging } from "firebase-admin/messaging";
import { createHash } from "crypto";

initializeApp();

export const notifyAdminsOnNewListing = onDocumentCreated(
  {
    document: "listings/{listingId}",
    region: "europe-west1",
  },
  async (event) => {
    const listing = event.data?.data();
    const listingId = event.params.listingId;
    if (!listing || listing.status !== "PAYMENT_PENDING") return;

    const tokenSnapshot = await getFirestore().collection("adminTokens").get();
    const tokens = tokenSnapshot.docs
      .map((doc) => doc.get("token"))
      .filter((token): token is string => typeof token === "string" && token.length > 20);
    if (tokens.length === 0) return;

    const title = typeof listing.title === "string" ? listing.title : "إعلان جديد";
    const response = await getMessaging().sendEachForMulticast({
      tokens,
      notification: {
        title: "إعلان جديد يحتاج المراجعة",
        body: title,
      },
      data: {
        type: "NEW_LISTING",
        listingId,
        title,
        body: "تمت إضافة إعلان جديد للمراجعة",
      },
      android: {
        priority: "high",
        notification: {
          channelId: "pending_listings",
        },
      },
    });

    const invalidTokens = response.responses
      .map((result, index) => ({ result, token: tokens[index] }))
      .filter(({ result }) => !result.success && (
        result.error?.code === "messaging/registration-token-not-registered" ||
        result.error?.code === "messaging/invalid-registration-token"
      ));

    await Promise.all(
      invalidTokens.map(({ token }) =>
        getFirestore().collection("adminTokens").doc(hashToken(token)).delete()
      )
    );
  },
);

function hashToken(token: string): string {
  // Token documents are keyed by SHA-256 in the Android client.
  return createHash("sha256").update(token).digest("hex");
}
