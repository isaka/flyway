---
subtitle: 'Tutorial: Checking permit expiry'
---
In this tutorial you will build a small Bash script that answers the question: *is my Flyway offline permit about to expire?*

_note: If you use a PAT token then it will refresh automatically and so you don't need to do this check. This tutorial is for those who use an offline permit._

This will take about 10 minutes.

## Before you start

You need:

- The [`jq`](https://jqlang.org/) tool is available
- You have licensed Flyway using an offline permit (not a PAT token - these refresh automatically so this tutorial is not relevant to you)
- A `version.json` file in the current folder containing the output of Flyway's `version` command. Produce one by running:

  ```bash
  flyway version -outputType=json > version.json
  ```

## Step 1: Find the field you need

Open `version.json` and look near the top. You will see something like this:

```
{
  "command" : "version",
  "edition" : "ENTERPRISE",
  "permitExpiry" : "2026-09-02",
```

There is the field you need: **`permitExpiry`**. Your date will be different
from the one above.

## Step 2: Pull out just the date

Hand the file to `jq` and ask for that one field:

```bash
jq -r '.permitExpiry' version.json
```

The output is a single line:

```
2026-09-02
```

`-r` asks `jq` for a *raw* string, so you get `2026-09-02` rather than
`"2026-09-02"` with quotes that would confuse the next step.

Store that date in a variable, because you are about to do arithmetic with it:

```bash
permit_expiry=$(jq -r '.permitExpiry' version.json)
echo "$permit_expiry"
```

You should see your expiry date printed again.

## Step 3: Count the days remaining

Bash cannot subtract two dates directly, so convert both to seconds-since-epoch,
subtract, and divide by the number of seconds in a day. `jq` can do all of this
for you:

```bash
days_remaining=$(jq -rn --arg expiry "$permit_expiry" '
  ($expiry | strptime("%Y-%m-%d") | mktime) as $expiry_epoch
  | (now | floor | . - (. % 86400)) as $today_epoch
  | ($expiry_epoch - $today_epoch) / 86400 | floor')
echo "$days_remaining days remaining"
```

You will see a number of days:

```
30 days remaining
```

`strptime` and `mktime` turn the expiry date into an epoch. `now` is the
current time including time-of-day, so `. - (. % 86400)` rounds it down to
midnight. Without that rounding you would be comparing midnight on the expiry
date against the current time of day, and your day count would round down by
one.

## Step 4: Turn the number into a verdict

A number on its own is not a check. Compare it against a threshold and say what
you found:

```bash
if [ "$days_remaining" -lt 30 ]; then
  echo "WARNING: permit expires in $days_remaining days"
else
  echo "OK: permit expires in $days_remaining days"
fi
```

You will see one of those two messages, depending on your own expiry date.

## Step 5: Assemble the script

We will add a few things to make the script more useful:

- The threshold defaults to 30 days, but you can override it by setting
  `THRESHOLD_DAYS` in the environment before running the script.

- We will call `flyway version` directly and check its output with
  `jq`, so the script does not depend on the `version.json` you created
  earlier and cannot go stale when you renew the permit.

- The warning branch ends in `exit 1` rather than just printing. That exit
  code is the part that makes this useful to automation.

### The finished script
We will call this `check-permit-expiry.sh` in later steps.
```bash
#!/usr/bin/env bash
set -euo pipefail

if [ -z "${THRESHOLD_DAYS:-}" ]; then
  THRESHOLD_DAYS=30
fi

if ! version_json=$(flyway version -outputType=json); then
  echo "ERROR: 'flyway version' failed:" >&2
  printf '%s\n' "$version_json" >&2
  exit 2
fi

permit_expiry=$(printf '%s' "$version_json" | jq -r '.permitExpiry // empty')

if [ -z "$permit_expiry" ]; then
  echo "ERROR: no permitExpiry field in the flyway version output." >&2
  exit 2
fi

days_remaining=$(jq -rn --arg expiry "$permit_expiry" '
  ($expiry | strptime("%Y-%m-%d") | mktime) as $expiry_epoch
  | (now | floor | . - (. % 86400)) as $today_epoch
  | ($expiry_epoch - $today_epoch) / 86400 | floor')

if [ "$days_remaining" -lt 0 ]; then
  echo "EXPIRED: Flyway permit expired on ${permit_expiry} ($(( -days_remaining )) day(s) ago)."
  exit 1
fi

if [ "$days_remaining" -lt "$THRESHOLD_DAYS" ]; then
  echo "WARNING: Flyway permit expires on ${permit_expiry} - ${days_remaining} day(s) remaining (threshold ${THRESHOLD_DAYS})."
  exit 1
fi

echo "OK: Flyway permit expires on ${permit_expiry} - ${days_remaining} day(s) remaining (threshold ${THRESHOLD_DAYS})."

```

## Step 6: Watch it fail

A check you have only ever seen succeed is a check you do not yet trust. Force
the failure by demanding ten years of permit:

```bash
chmod +x check-permit-expiry.sh
THRESHOLD_DAYS=3650 ./check-permit-expiry.sh
```

The wording changes from `OK` to `WARNING` and the exit code changes to `1` (failure).

```
WARNING: Flyway permit expires on 2026-09-02 - 30 day(s) remaining (threshold 3650).
```


## What you have done

You started from Flyway's `version.json`, and finished with a script that
reduces it to one line and one exit code. Along the way you:

- read a single field out of JSON with `jq`
- did date arithmetic in Bash by converting to epoch seconds
- signalled a problem through an exit code rather than only through text

That last point is what makes this script ready for use in a pipeline - it fails the build when that code is non-zero.

You could set up a workflow that checks the permit status periodically (overnight) and the failure of this workflow will give you the indication that you need to renew your Flyway offline permit before your production pipeline is affected.
