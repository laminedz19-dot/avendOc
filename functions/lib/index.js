"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.notifyAdminsOnNewListing = void 0;
const firestore_1 = require("firebase-functions/v2/firestore");
const app_1 = require("firebase-admin/app");
const firestore_2 = require("firebase-admin/firestore");
const messaging_1 = require("firebase-admin/messaging");
const crypto_1 = require("crypto");
(0, app_1.initializeApp)();
exports.notifyAdminsOnNewListing = (0, firestore_1.onDocumentCreated)({
    document: "listings/{listingId}",
    region: "europe-west1",
}, async (event) => {
    const listing = event.data?.data();
    const listingId = event.params.listingId;
    if (!listing || listing.status !== "PAYMENT_PENDING")
        return;
    const tokenSnapshot = await (0, firestore_2.getFirestore)().collection("adminTokens").get();
    const tokens = tokenSnapshot.docs
        .map((doc) => doc.get("token"))
        .filter((token) => typeof token === "string" && token.length > 20);
    if (tokens.length === 0)
        return;
    const title = typeof listing.title === "string" ? listing.title : "إعلان جديد";
    const response = await (0, messaging_1.getMessaging)().sendEachForMulticast({
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
        .filter(({ result }) => !result.success && (result.error?.code === "messaging/registration-token-not-registered" ||
        result.error?.code === "messaging/invalid-registration-token"));
    await Promise.all(invalidTokens.map(({ token }) => (0, firestore_2.getFirestore)().collection("adminTokens").doc(hashToken(token)).delete()));
});
function hashToken(token) {
    // Token documents are keyed by SHA-256 in the Android client.
    return (0, crypto_1.createHash)("sha256").update(token).digest("hex");
}
//# sourceMappingURL=index.js.map