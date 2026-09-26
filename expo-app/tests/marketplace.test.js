import { describe, expect, it } from "vitest";
import fs from "node:fs";
import path from "node:path";

const projectRoot = process.cwd();

describe("avendOc Expo migration contracts", () => {
  it("keeps all marketplace category identifiers", () => {
    const source = fs.readFileSync(path.join(projectRoot, "types/marketplace.ts"), "utf8");
    for (const id of ["vehicles", "real_estate", "phones_tech", "appliances", "fashion", "furniture", "tools", "services", "other"]) {
      expect(source).toContain(`id: "${id}"`);
    }
  });

  it("uses the secure admin claim and pending approval flow", () => {
    const authSource = fs.readFileSync(path.join(projectRoot, "hooks/use-auth.tsx"), "utf8");
    const dataSource = fs.readFileSync(path.join(projectRoot, "lib/marketplace.ts"), "utf8");
    const adminSource = fs.readFileSync(path.join(projectRoot, "app/(tabs)/admin.tsx"), "utf8");
    expect(authSource).toContain("token.claims.admin === true");
    expect(dataSource).toContain('status: "PAYMENT_PENDING"');
    expect(adminSource).toContain("isAdmin");
  });
});
