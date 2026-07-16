# ─────────────────────────────────────────────────────────────────────────
# Build context NOTE: this Dockerfile COPYs from sibling project directories
# (../autoFrameX, ../alfaDock) because GPN depends on 2 other local,
# unpublished Maven artifacts and no shared Nexus/Artifactory exists in this
# environment. Build from the workspace/ PARENT directory, not from GPN/
# itself:
#
#   cd "D:\E Drive\Engineering\testleaf\workspace"
#   docker build -f GPN/Dockerfile -t gpn-tests .
#
# The real, longer-term industry-practice fix is a shared internal Maven
# repository (Nexus/Artifactory/GitHub Packages) that autoFrameX and AlfaDOCK
# publish to and GPN's pom.xml resolves from normally — this build-context
# workaround is a deliberate v1 simplification, not the end state.
# ─────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17

LABEL org.opencontainers.image.title="GPN"
LABEL org.opencontainers.image.description="GPN Selenium/TestNG suite — built on autoFrameX + AlfaDOCK"

# Install Chrome via official Google .deb (stable, Debian-compatible base image)
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends \
        wget \
        gnupg \
        ca-certificates \
        fonts-liberation \
        libappindicator3-1 \
        libasound2 \
        libatk-bridge2.0-0 \
        libatk1.0-0 \
        libcups2 \
        libdbus-1-3 \
        libgdk-pixbuf2.0-0 \
        libnspr4 \
        libnss3 \
        libx11-xcb1 \
        libxcomposite1 \
        libxdamage1 \
        libxrandr2 \
        xdg-utils && \
    wget -q -O /tmp/google-chrome.deb \
        https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt-get install -y --no-install-recommends /tmp/google-chrome.deb && \
    rm /tmp/google-chrome.deb && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN google-chrome --version

WORKDIR /app

# ── Layer 1: dependency cache — every pom, so go-offline sees the full graph ──
COPY autoFrameX/pom.xml autoFrameX/
COPY autoFrameX/autoframex-core/pom.xml autoFrameX/autoframex-core/
COPY autoFrameX/autoframex-selenium/pom.xml autoFrameX/autoframex-selenium/
COPY autoFrameX/autoframex-api/pom.xml autoFrameX/autoframex-api/
COPY autoFrameX/autoframex-database/pom.xml autoFrameX/autoframex-database/
COPY autoFrameX/autoframex-cucumber/pom.xml autoFrameX/autoframex-cucumber/
COPY autoFrameX/autoframex-performance/pom.xml autoFrameX/autoframex-performance/
COPY autoFrameX/autoframex-security/pom.xml autoFrameX/autoframex-security/
COPY autoFrameX/autoframex-testkit/pom.xml autoFrameX/autoframex-testkit/
COPY alfaDock/pom.xml alfaDock/
COPY GPN/pom.xml GPN/
RUN mvn -f autoFrameX/pom.xml dependency:go-offline -q

# ── Layer 2: source — invalidated on any source change ──
COPY autoFrameX/autoframex-core/ autoFrameX/autoframex-core/
COPY autoFrameX/autoframex-selenium/ autoFrameX/autoframex-selenium/
COPY autoFrameX/autoframex-api/ autoFrameX/autoframex-api/
COPY autoFrameX/autoframex-database/ autoFrameX/autoframex-database/
COPY autoFrameX/autoframex-cucumber/ autoFrameX/autoframex-cucumber/
COPY autoFrameX/autoframex-performance/ autoFrameX/autoframex-performance/
COPY autoFrameX/autoframex-security/ autoFrameX/autoframex-security/
COPY autoFrameX/autoframex-testkit/ autoFrameX/autoframex-testkit/
COPY alfaDock/src/ alfaDock/src/
COPY GPN/src/ GPN/src/
COPY GPN/*.xml GPN/
# GPN's own account fixtures ("GPN Account.xlsx" etc.) are deliberately NOT
# copied here — they hold real customer credentials (see GPN/.gitignore) and
# must be supplied at container runtime via a mounted volume instead:
#   -v "$(pwd)/GPN Account.xlsx:/app/GPN/src/test/resources/GPN Account.xlsx:ro"

# Install in dependency order: autoFrameX reactor -> AlfaDOCK -> GPN.
# install (not compile): downstream builds need upstream artifacts resolvable
# from the local repo, not just compiled in-place.
RUN mvn -f autoFrameX/pom.xml clean install -DskipTests -Djacoco.skip=true -q && \
    mvn -f alfaDock/pom.xml clean install -DskipTests -q && \
    mvn -f GPN/pom.xml clean install -DskipTests -q

WORKDIR /app/GPN

# Runtime defaults — all overridable via --env at docker run or in docker-compose.yml
ENV BROWSER=chrome
ENV HEADLESS=true
ENV ENVIRONMENT=dev
ENV SUITE_FILE=gpn.xml

# Credentials — never baked into the image. Supply real values via
# `docker run --env-file .env.gpn` (see docker-compose.yml). These fall
# through AlfaDockBaseTest's ConfigResolver-based STANDARD-mode resolution;
# DATAPROVIDER-mode suites (gpn.xml's default) read credentials from the
# mounted Excel fixture instead, not these env vars.
ENV ALFADOCK_COMPANY_USER=""
ENV ALFADOCK_COMPANY_PASS=""
ENV ALFADOCK_USER_USER=""
ENV ALFADOCK_USER_PASS=""

# Mount these volumes to retrieve test artifacts from the host after the container exits
VOLUME ["/app/GPN/reports", "/app/GPN/logs", "/app/GPN/target/surefire-reports"]

# Shell-form ENTRYPOINT so ${ENV_VAR} values expand at container runtime
ENTRYPOINT mvn test \
    -DsuiteXmlFile=${SUITE_FILE} \
    -Dbrowser=${BROWSER} \
    -Denv=${ENVIRONMENT} \
    -Dheadless=${HEADLESS}
